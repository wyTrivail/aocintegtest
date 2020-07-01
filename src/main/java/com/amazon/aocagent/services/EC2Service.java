package com.amazon.aocagent.services;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.ResourceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.log4j.Log4j2;


/** EC2Service is a wrapper of Amazon EC2 Client. */
@Log4j2
public class EC2Service {
  private AmazonEC2 amazonEC2;

  public EC2Service(String region) {
    amazonEC2 = AmazonEC2ClientBuilder.standard().withRegion(region).build();
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
            .withKeyName(GenericConstants.SSH_KEY_NAME.getVal());

    RunInstancesResult runInstancesResult = amazonEC2.runInstances(runInstancesRequest);

    // return the first instance since only one instance gets launched
    Instance instance = runInstancesResult.getReservation().getInstances().get(0);

    // return the instance until it's ready
    return getInstanceUntilReady(instance.getInstanceId());
  }

  private Instance getInstanceUntilReady(String targetInstanceId) throws Exception {
    DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
        .withInstanceIds(targetInstanceId);


    AtomicReference<Instance> runningInstance = new AtomicReference<>();
    RetryHelper.retry(() -> {
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
}
