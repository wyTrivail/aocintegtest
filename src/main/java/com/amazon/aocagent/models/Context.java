package com.amazon.aocagent.models;

import com.amazon.aocagent.fileconfigs.ExpectedMetric;
import com.amazon.aocagent.fileconfigs.OTConfig;
import com.amazon.aocagent.fileconfigs.ExpectedTrace;
import com.amazon.aocagent.testamis.ITestAMI;
import com.amazonaws.services.ec2.model.Subnet;
import lombok.Data;

import java.util.List;

@Data
public class Context {
  private String stackFilePath;
  private Stack stack;
  private String agentVersion;
  private String localPackagesDir;
  private ITestAMI testingAMI;
  private String instanceId;
  private String instancePublicIpAddress;
  private String githubSha;
  private OTConfig otConfig;
  private ExpectedMetric expectedMetric;
  private ExpectedTrace expectedTrace;
  private String expectedTraceId;
  private List<String> expectedSpanIdList;

  /** AWS account default Security Group Id. */
  private String defaultSecurityGrpId;

  /** AWS account default VPC Id. */
  private String defaultVpcId;

  /** AWS account default subnets. */
  private List<Subnet> defaultSubnets;

  /** ECS Service launch type. Eg, EC2 or Fargate. */
  private String ecsLaunchType;

  /** ECS deployment mode. Eg, SIDECAR or DaemonSet. */
  private String ecsDeploymentMode;

  /** ECS cluster name. */
  private String clusterName;

  /** ECS task role arn. */
  private String taskRoleArn;

  /** ECS task execution role arn. */
  private String executionRoleArn;

  /** ECS data emitter image. */
  private String dataEmitterImage;

  /** AOC image for testing. */
  private String aocImage;

  /** Test resources region. */
  private String region;

}
