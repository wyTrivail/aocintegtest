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
  private String launchType;
  private String deploymentMode;
  private String defaultSecurityGrpId;
  private String defaultVpcId;
  private List<Subnet> defaultSubnets;
  private String ecsContainerInstance;
}
