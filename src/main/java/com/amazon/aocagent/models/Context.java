package com.amazon.aocagent.models;

import com.amazon.aocagent.enums.ExpectedMetric;
import com.amazon.aocagent.enums.OTConfig;
import com.amazon.aocagent.testamis.ITestAMI;
import lombok.Data;

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
}
