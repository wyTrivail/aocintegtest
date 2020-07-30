package com.amazon.aocagent.models;

import com.amazon.aocagent.enums.TestAMI;
import lombok.Data;

@Data
public class Context {
  private Stack stack;
  private String agentVersion;
  private String localPackagesDir;
  private TestAMI testingAMI;
  private String instanceID;
  private String instancePublicIpAddress;
  private String githubSha;
}
