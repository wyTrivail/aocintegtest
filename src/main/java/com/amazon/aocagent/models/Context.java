package com.amazon.aocagent.models;

import com.amazon.aocagent.fileconfigs.ExpectedMetric;
import com.amazon.aocagent.fileconfigs.ExpectedTrace;
import com.amazon.aocagent.fileconfigs.OTConfig;
import com.amazon.aocagent.testamis.ITestAMI;
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
}
