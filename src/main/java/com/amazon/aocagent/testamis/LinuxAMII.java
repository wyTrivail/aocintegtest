package com.amazon.aocagent.testamis;

import java.util.Arrays;
import java.util.List;

public abstract class LinuxAMII implements ITestAMI {
  @Override
  public abstract String getAMIId();

  @Override
  public abstract String getLoginUser();

  @Override
  public abstract String getS3Key(String version);

  @Override
  public abstract String getPackageName();

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
}
