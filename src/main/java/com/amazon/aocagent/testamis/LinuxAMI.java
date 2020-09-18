package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.Architecture;
import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.enums.OSType;
import com.amazon.aocagent.enums.S3Package;
import com.amazonaws.services.ec2.model.InstanceType;

public abstract class LinuxAMI implements ITestAMI {
  private String amiId;
  private boolean useSSM;

  public LinuxAMI(String amiId) {
    this(amiId, false);
  }

  public LinuxAMI(String amiId, boolean useSSM) {
    this.amiId = amiId;
    this.useSSM = useSSM;
  }

  @Override
  public String getAMIId() {
    return this.amiId;
  }

  @Override
  public boolean isUseSSM() {
    return useSSM;
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
  public String getConfiguringCommand(String configContent) {
    return String.format(
            "(\n" + "cat<<EOF\n" + "%s\n" + "EOF\n" + ") | sudo tee %s",
            configContent, GenericConstants.EC2_CONFIG_PATH.getVal());
  }

  @Override
  public String getStartingCommand(String configPath) {
    return String.format(
            "sudo %s -c %s -a start",
            "/opt/aws/aws-observability-collector/bin/aws-observability-collector-ctl", configPath);
  }

  @Override
  public String getDisableFirewallCommand() {
    return null;
  }

  @Override
  public String getSSMDocument() {
    // TODO: modify this once we add SSM test for linux
    return null;
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
