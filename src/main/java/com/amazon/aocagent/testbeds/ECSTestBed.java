package com.amazon.aocagent.testbeds;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.models.EC2InstanceParams;
import com.amazon.aocagent.services.AwsNetworkService;
import com.amazon.aocagent.services.EC2Service;
import com.amazon.aocagent.services.ECSService;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.ResourceType;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;
import lombok.extern.log4j.Log4j2;

import java.util.Base64;

@Log4j2
public class ECSTestBed implements TestBed {

  private static String CONTAINER_INSTANCE_USER_DATA =
      "#!/bin/bash\n" + "echo ECS_CLUSTER=%s >> /etc/ecs/ecs.config";
  private ECSService ecsService;
  private EC2Service ec2Service;
  private AwsNetworkService networkService;
  private Context context;

  @Override
  public void init(Context context) {
    this.context = context;
    this.ecsService = new ECSService(context.getStack().getTestingRegion());
    this.ec2Service = new EC2Service(context.getStack().getTestingRegion());
    this.networkService = new AwsNetworkService(context.getStack().getTestingRegion());
  }

  /**
   * run AOC and data emitter on ECS fargate and EC2 instances.
   * @return context params after setup ECS test bed
   * @throws Exception failed to launch testbed
   */
  @Override
  public Context launchTestBed() throws Exception {
    try {
      // create ECS cluster
      if (!ecsService.describeCluster(GenericConstants.ECS_SIDECAR_CLUSTER.getVal()).isPresent()) {
        ecsService.createCluster();
      }
      // get the default security group, vpc and subnets
      // from the provided aws account
      networkService.buildNetworkContext(context);

      // launch new EC2 container instance for EC2 mode
      if (context.getLaunchType().equalsIgnoreCase("EC2")
          && !ecsService.isContainerInstanceAvail(GenericConstants.ECS_SIDECAR_CLUSTER.getVal())) {
        log.info("launching up a container instance");
        EC2InstanceParams ec2InstanceParams = this.buildEc2ConfigForEcs(context);
        Instance containerInstance = ec2Service.launchInstance(ec2InstanceParams);
        context.setEcsContainerInstance(containerInstance.getInstanceId());
        log.info(
            "created new ECS container instance: {} - {} ",
            containerInstance.getInstanceId(),
            containerInstance.getState().getName());
        ecsService.waitForContainerInstanceRegistered(context);
      }

    } catch (Exception e) {
      log.error("ECS launchTestBed failed: {}", e);
      throw e;
    }
    return this.context;
  }

  /**
   * build launching config for EC2 container instance.
   * @param context test context
   * @return {@link EC2InstanceParams} ecs launch params
   */
  private EC2InstanceParams buildEc2ConfigForEcs(Context context) {
    // tag instance for management
    TagSpecification tagSpecification =
        new TagSpecification()
            .withResourceType(ResourceType.Instance)
            .withTags(
                new Tag(
                    GenericConstants.EC2_INSTANCE_TAG_KEY.getVal(),
                    GenericConstants.EC2_INSTANCE_ECS_TAG_VAL.getVal()));
    String userData =
        Base64.getEncoder()
            .encodeToString(
                String.format(
                        CONTAINER_INSTANCE_USER_DATA, GenericConstants.ECS_SIDECAR_CLUSTER.getVal())
                    .getBytes());
    return EC2InstanceParams.builder()
        .amiId(GenericConstants.ECS_EC2_AMI_ID.getVal())
        .iamRoleName(GenericConstants.ECS_IAM_ROLE_NAME.getVal())
        .securityGrpName("default")
        .tagSpecification(tagSpecification)
        .sshKeyName(GenericConstants.SSH_KEY_NAME.getVal())
        .userData(userData)
        .build();
  }
}
