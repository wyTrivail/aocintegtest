package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.Architecture;
import com.amazon.aocagent.enums.S3Package;
import com.amazonaws.services.ec2.model.InstanceType;

import java.util.Arrays;
import java.util.List;

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
        "/opt/aws/aws-opentelemetry-collector/bin/aws-opentelemetry-collector-ctl", configPath);
  }

  @Override
  public List<String> getDockerInstallingCommands() {
    return Arrays.asList(
        "sudo yum update -y",
        "sudo yum install -y docker",
        "sudo service docker start",
        String.format("sudo usermod -a -G docker %s", this.getLoginUser()));
  }

  @Override
  public InstanceType getInstanceType() {
    if (getS3Package().getLocalPackage().getArchitecture() == Architecture.ARM64) {
      return InstanceType.A1Medium;
    }
    return InstanceType.T2Medium;
  }
}
