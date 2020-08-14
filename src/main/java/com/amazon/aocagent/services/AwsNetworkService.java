package com.amazon.aocagent.services;

import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.models.Context;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.SecurityGroup;

public class AwsNetworkService {

  private AmazonEC2 ec2Client;
  private String region;

  /**
   * retrieve network param for running ECS.
   * @param region client region
   */
  public AwsNetworkService(String region) {
    this.region = region;
    ec2Client = AmazonEC2ClientBuilder.standard().withRegion(region).build();
  }

  /**
   * retrieve the essential networking params for running ECS tasks.
   * @param context test context
   * @throws Exception fail to describe network
   */
  public void buildNetworkContext(Context context) throws Exception {
    this.describeSecurityGroup(context);
    this.describeSubnets(context);
  }

  /**
   * describe the account default security group and get both group id and vpc id.
   * @param context test context
   * @throws Exception fail to describe sec group
   */
  public void describeSecurityGroup(Context context) throws Exception {
    DescribeSecurityGroupsRequest request =
        new DescribeSecurityGroupsRequest().withGroupNames("default");
    DescribeSecurityGroupsResult result = ec2Client.describeSecurityGroups(request);
    if (result.getSecurityGroups().isEmpty()) {
      throw new BaseException(ExceptionCode.NO_DEFAULT_SECURITY_GROUP);
    }

    SecurityGroup defaultGroup = result.getSecurityGroups().get(0);
    context.setDefaultSecurityGrpId(defaultGroup.getGroupId());
    context.setDefaultVpcId(defaultGroup.getVpcId());
  }

  /**
   * describe the subnets by default VPC id.
   * @param context test context
   * @throws Exception fail to describe subnets
   */
  public void describeSubnets(Context context) throws Exception {
    DescribeSubnetsRequest request =
        new DescribeSubnetsRequest()
            .withFilters(new Filter().withName("vpc-id").withValues(context.getDefaultVpcId()));
    DescribeSubnetsResult result = ec2Client.describeSubnets(request);
    if (result.getSubnets().isEmpty()) {
      throw new BaseException(ExceptionCode.NO_AVAILABLE_SUBNET);
    }
    context.setDefaultSubnets(result.getSubnets());
  }
}
