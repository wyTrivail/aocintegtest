package com.amazon.aocagent.models;

import com.amazon.aocagent.enums.ExpectedMetric;
import com.amazon.aocagent.enums.OTConfig;
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

  /** ECS Service launch type. Eg, EC2 or Fargate */
  private String launchType;

  /** ECS deployment mode. Eg, SIDECAR or DaemonSet */
  private String deploymentMode;

  /** AWS account default Security Group Id */
  private String defaultSecurityGrpId;

  /** AWS account default VPC Id */
  private String defaultVpcId;

  /** AWS account default subnets */
  private List<Subnet> defaultSubnets;

}
