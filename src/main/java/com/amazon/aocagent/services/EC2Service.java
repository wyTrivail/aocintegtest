package com.amazon.aocagent.services;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.ResourceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/** EC2Service is a wrapper of Amazon EC2 Client. */
@Log4j2
public class EC2Service {
  private AmazonEC2 amazonEC2;
  private String region;
  private S3Service s3Service;
  private static final String ERROR_CODE_KEY_PAIR_NOT_FOUND = "InvalidKeyPair.NotFound";
  private static final String ERROR_CODE_KEY_PAIR_ALREADY_EXIST = "InvalidKeyPair.Duplicate";
  private static final String ERROR_CODE_SECURITY_GROUP_ALREADY_EXIST = "InvalidGroup.Duplicate";
  private static final String ERROR_CODE_SECURITY_GROUP_NOT_FOUND = "InvalidGroup.NotFound";



  /**
   * Construct ec2 service base on region.
   * @param region the region to launch ec2 instance
   */
  public EC2Service(String region) {
    this.region = region;
    amazonEC2 = AmazonEC2ClientBuilder.standard().withRegion(region).build();
    s3Service = new S3Service(region);
  }

  /**
   * launchInstance launches one ec2 instance.
   *
   * @param amiID the instance amiid
   * @return InstanceID
   */
  public Instance launchInstance(String amiID) throws Exception {
    // tag instance for management
    TagSpecification tagSpecification =
        new TagSpecification()
            .withResourceType(ResourceType.Instance)
            .withTags(
                new Tag(
                    GenericConstants.EC2_INSTANCE_TAG_KEY.getVal(),
                    GenericConstants.EC2_INSTANCE_TAG_VAL.getVal()));

    // create request
    RunInstancesRequest runInstancesRequest =
        new RunInstancesRequest()
            .withImageId(amiID)
            .withMonitoring(false)
            .withMaxCount(1)
            .withMinCount(1)
            .withTagSpecifications(tagSpecification)
            .withKeyName(GenericConstants.SSH_KEY_NAME.getVal())
            .withSecurityGroupIds(
                getOrCreateSecurityGroupByName(GenericConstants.SECURITY_GROUP_NAME.getVal()))
            .withIamInstanceProfile(
                new IamInstanceProfileSpecification()
                    .withName(GenericConstants.IAM_ROLE_NAME.getVal())
            );

    // create ssh key if not existed
    createSSHKeyIfNotExisted(GenericConstants.SSH_KEY_NAME.getVal());

    RunInstancesResult runInstancesResult = amazonEC2.runInstances(runInstancesRequest);

    // return the first instance since only one instance gets launched
    Instance instance = runInstancesResult.getReservation().getInstances().get(0);

    // return the instance until it's ready
    return getInstanceUntilReady(instance.getInstanceId());
  }

  /**
   * listInstancesByTag gets ec2 instance info list based on the tag.
   *
   * @param tagName tag key name
   * @param tagValue tag value
   * @return the list of ec2 instance
   */
  public List<Instance> listInstancesByTag(String tagName, String tagValue) {
    DescribeInstancesRequest describeInstancesRequest =
        new DescribeInstancesRequest()
            .withFilters(new Filter("tag:" + tagName).withValues(tagValue));

    List<Instance> instanceList = new ArrayList<>();

    while (true) {
      DescribeInstancesResult describeInstancesResult =
          amazonEC2.describeInstances(describeInstancesRequest);
      for (Reservation reservation : describeInstancesResult.getReservations()) {
        instanceList.addAll(reservation.getInstances());
      }

      describeInstancesRequest.setNextToken(describeInstancesResult.getNextToken());
      if (describeInstancesRequest.getNextToken() == null) {
        return instanceList;
      }
    }
  }

  /**
   * terminateInstance terminates ec2 instances base on the instance id list.
   *
   * @param instanceIds ec2 instance ids to be terminated
   */
  public void terminateInstance(List<String> instanceIds) {
    if (instanceIds.size() == 0) {
      return;
    }
    TerminateInstancesRequest terminateInstancesRequest =
        new TerminateInstancesRequest().withInstanceIds(instanceIds);

    amazonEC2.terminateInstances(terminateInstancesRequest);
  }

  private Instance getInstanceUntilReady(String targetInstanceId) throws Exception {
    DescribeInstancesRequest describeInstancesRequest =
        new DescribeInstancesRequest().withInstanceIds(targetInstanceId);

    AtomicReference<Instance> runningInstance = new AtomicReference<>();
    RetryHelper.retry(
        () -> {
          DescribeInstancesResult describeInstancesResult =
              amazonEC2.describeInstances(describeInstancesRequest);
          for (Reservation reservation : describeInstancesResult.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
              if (!targetInstanceId.equals(instance.getInstanceId())) {
                continue;
              }
              String instanceStateName = instance.getState().getName();
              if (!InstanceStateName.Running.toString().equals(instanceStateName)) {
                throw new BaseException(ExceptionCode.EC2INSTANCE_STATUS_PENDING);
              }
              log.info("instance network is ready");
              runningInstance.set(instance);
            }
          }
        });

    return runningInstance.get();
  }

  /**
   * createSSHKeyIfNotExisted creates the ssh keypair for ec2 login and
   * upload it to s3 private bucket for future usage.
   * @param keyPairName the keypair name used to login
   * @throws IOException on failing to write keypair to disk
   * @throws BaseException on s3 uploading failure
   */
  public void createSSHKeyIfNotExisted(String keyPairName) throws IOException, BaseException {
    if (isKeyPairExisted(keyPairName)) {
      return;
    }

    try {
      // create keypair
      CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
      createKeyPairRequest.setKeyName(keyPairName);
      CreateKeyPairResult createKeyPairResult = amazonEC2.createKeyPair(createKeyPairRequest);
      String keyMaterial = createKeyPairResult.getKeyPair().getKeyMaterial();

      // store the keypair to s3 for future usage
      String keyPairFileName = keyPairName + ".pem";
      String keyPairLocalPath = "/tmp/" + keyPairFileName;
      FileUtils.writeStringToFile(new File(keyPairLocalPath), keyMaterial);
      S3Service s3Service = new S3Service(region);
      s3Service.uploadS3ObjectWithPrivateAccess(
          keyPairLocalPath, GenericConstants.SSH_KEY_S3_BUCKET.getVal(), keyPairFileName, false);

    } catch (AmazonEC2Exception e) {
      if (!ERROR_CODE_KEY_PAIR_ALREADY_EXIST.equals(e.getErrorCode())) {
        throw e;
      }
    }
  }

  private boolean isKeyPairExisted(String keyPairName) {
    try {
      DescribeKeyPairsRequest describeKeyPairsRequest = new DescribeKeyPairsRequest();
      describeKeyPairsRequest.setKeyNames(Collections.singletonList(keyPairName));
      DescribeKeyPairsResult describeKeyPairsResult =
          amazonEC2.describeKeyPairs(describeKeyPairsRequest);
      List<KeyPairInfo> keyPairInfoList = describeKeyPairsResult.getKeyPairs();
      if (keyPairInfoList.isEmpty()) {
        return false;
      }
    } catch (AmazonEC2Exception e) {
      if (ERROR_CODE_KEY_PAIR_NOT_FOUND.equals(e.getErrorCode())) {
        return false;
      } else {
        throw e;
      }
    }
    return true;
  }

  public void downloadSSHKey(String keyPairName, String toLocation) {
    s3Service.downloadS3Object(
        GenericConstants.SSH_KEY_S3_BUCKET.getVal(), keyPairName + ".pem", toLocation);
  }

  private String getOrCreateSecurityGroupByName(String groupName) {
    try {
      return getSecurityGroupByName(groupName);
    } catch (AmazonEC2Exception e) {
      if (ERROR_CODE_SECURITY_GROUP_NOT_FOUND.equals(e.getErrorCode())) {
        return createSecurityGroup(groupName);
      }
      throw e;
    }
  }

  private String getSecurityGroupByName(String groupName) {
    final List<SecurityGroup> securityGroups = amazonEC2
        .describeSecurityGroups(new DescribeSecurityGroupsRequest().withGroupNames(groupName))
        .getSecurityGroups();
    return securityGroups.get(0).getGroupId();
  }

  private String createSecurityGroup(String groupName) {
    try {
      //get the vpcId of default, it is always there and cannot be deleted.
      List<SecurityGroup> securityGroups = amazonEC2
          .describeSecurityGroups(new DescribeSecurityGroupsRequest()
              .withGroupNames(GenericConstants.DEFAULT_SECURITY_GROUP_NAME.getVal()))
          .getSecurityGroups();
      if (securityGroups.size() <= 0) {
        throw new RuntimeException("Cannot get the default security group.");
      }
      String vpcId = securityGroups.get(0).getVpcId();

      //create new security group and get the group id
      String groupId = amazonEC2
          .createSecurityGroup(new CreateSecurityGroupRequest().withGroupName(groupName)
          .withDescription(groupName + " used for aoc integration test")
              .withVpcId(vpcId)).getGroupId();

      //add the incoming ip request permission
      IpPermission sshIpPermission = new IpPermission();
      sshIpPermission.withIpv4Ranges(new IpRange().withCidrIp("0.0.0.0/0"))
          .withIpProtocol("tcp").withFromPort(22).withToPort(22);

      IpPermission rdpIpPermission = new IpPermission();
      rdpIpPermission.withIpv4Ranges(new IpRange().withCidrIp("0.0.0.0/0"))
          .withIpProtocol("tcp").withFromPort(3389).withToPort(3389);

      amazonEC2.authorizeSecurityGroupIngress(
          new AuthorizeSecurityGroupIngressRequest()
              .withGroupId(groupId).withIpPermissions(sshIpPermission, rdpIpPermission)
      );

      return groupId;
    } catch (AmazonEC2Exception e) {
      if (ERROR_CODE_SECURITY_GROUP_ALREADY_EXIST.equals(e.getErrorCode())) {
        return getSecurityGroupByName(groupName);
      }
      throw e;
    }
  }
}
