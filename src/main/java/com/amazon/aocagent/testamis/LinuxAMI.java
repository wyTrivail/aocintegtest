package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.Architecture;
import com.amazon.aocagent.enums.S3Package;
import com.amazonaws.services.ec2.model.InstanceType;

public abstract class LinuxAMI implements ITestAMI {
  private String amiId;

  public LinuxAMI(String amiId) {
    this.amiId = amiId;
  }

  @Override
  public String getAMIId() {
    return this.amiId;
  }

  @Override
  public abstract String getLoginUser();

  @Override
  public abstract S3Package getS3Package();

  @Override
  public String getDownloadingCommand(String fromUrl, String toLocation) {
    return String.format("curl -L %s -o %s", fromUrl, toLocation);
  }

  @Override
  public String getInstallingCommand(String packagePath) {
    return String.format("sudo rpm -Uvh %s", packagePath);
  }

  @Override
  public String getStartingCommand(String configPath) {
    return String.format(
        "sudo %s -c %s -a start",
        "/opt/aws/aws-observability-collector/bin/aws-observability-collector-ctl", configPath);
  }

  @Override
  public InstanceType getInstanceType() {
    if (getS3Package().getLocalPackage().getArchitecture() == Architecture.ARM64) {
      return InstanceType.A1Medium; // t2medium can't apply to arm instances.
    }
    return InstanceType.T2Medium;
  }

  @Override
  public String getIptablesCommand() {
    // in most of the case we don't need to handle iptables except for centos6
    return null;
  }
}
