package com.amazon.aocagent.services;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AddRoleToInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreateInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleResult;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.identitymanagement.model.GetRoleResult;
import com.amazonaws.services.identitymanagement.model.NoSuchEntityException;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class IAMService {
  private static final String DNS_SUFFIX = ".amazonaws.com";
  private static final String assumeEC2RolePolicyDoc =
      "{\"Version\":\"2012-10-17\","
          + "\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"Service\":[\"ec2"
          + DNS_SUFFIX
          + "\"]},\"Action\":[\"sts:AssumeRole\"]}]}";
  private AmazonIdentityManagement amazonIdentityManagement;
  private Region region;

  /**
   * Construct IAMService with region.
   *
   * @param region the region to build IAM
   */
  public IAMService(String region) {
    amazonIdentityManagement =
        AmazonIdentityManagementClientBuilder.standard().withRegion(region).build();
    this.region = Region.getRegion(Regions.fromName(region));
  }

  /**
   * createIAMRoleIfNotExisted creates/returns the iam role if it's not existed.
   *
   * @param iamRoleName the iam role name
   * @return the iam role arn.
   */
  public String createIAMRoleIfNotExisted(String iamRoleName) {
    try {
      return getRoleArn(iamRoleName);
    } catch (NoSuchEntityException ex) {
      return createIAMRole(iamRoleName);
    }
  }

  private String getRoleArn(String iamRoleName) {
    GetRoleResult getRoleResult =
        amazonIdentityManagement.getRole(new GetRoleRequest().withRoleName(iamRoleName));

    return getRoleResult.getRole().getArn();
  }

  private String createIAMRole(String iamRoleName) {
    CreateRoleRequest createRoleRequest = new CreateRoleRequest();
    createRoleRequest.setDescription("Used for AOC integration tests.");
    createRoleRequest.setPath("/");
    createRoleRequest.setRoleName(iamRoleName);
    createRoleRequest.setMaxSessionDuration(3600); // 1 hour
    createRoleRequest.setAssumeRolePolicyDocument(assumeEC2RolePolicyDoc);

    CreateRoleResult createRoleResult = amazonIdentityManagement.createRole(createRoleRequest);
    final String roleArn = createRoleResult.getRole().getArn();

    for (String policy :
        Arrays.asList(
            String.format(
                "arn:%s:iam::aws:policy/CloudWatchAgentServerPolicy", region.getPartition()),
            String.format("arn:%s:iam::aws:policy/AWSXrayFullAccess", region.getPartition()),
            String.format("arn:%s:iam::aws:policy/AmazonS3FullAccess", region.getPartition()))) {
      AttachRolePolicyRequest attachRolePolicyRequest = new AttachRolePolicyRequest();
      attachRolePolicyRequest.setRoleName(iamRoleName);
      attachRolePolicyRequest.setPolicyArn(policy);
      amazonIdentityManagement.attachRolePolicy(attachRolePolicyRequest);
    }

    CreateInstanceProfileRequest createInstanceProfileRequest = new CreateInstanceProfileRequest();
    createInstanceProfileRequest.setInstanceProfileName(iamRoleName);
    createInstanceProfileRequest.setPath("/");
    amazonIdentityManagement.createInstanceProfile(createInstanceProfileRequest);

    AddRoleToInstanceProfileRequest addRoleToInstanceProfileRequest =
        new AddRoleToInstanceProfileRequest();
    addRoleToInstanceProfileRequest.setRoleName(iamRoleName);
    addRoleToInstanceProfileRequest.setInstanceProfileName(iamRoleName);
    amazonIdentityManagement.addRoleToInstanceProfile(addRoleToInstanceProfileRequest);

    return roleArn;
  }
}
