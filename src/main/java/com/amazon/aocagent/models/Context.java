package com.amazon.aocagent.models;

import com.amazon.aocagent.enums.Stack;
import com.amazon.aocagent.enums.TestAMI;
import lombok.Data;

@Data
public class Context {
  private Stack stack;
  private String agentVersion;
  private String localPackagesDir;
  private String region;
  private TestAMI testingAMI;
  private String sshCertPath;
}

