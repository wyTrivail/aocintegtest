package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

import java.util.Arrays;
import java.util.List;

public abstract class DebianAMI extends LinuxAMI {
  @Override
  public abstract String getAMIId();

  @Override
  public abstract String getLoginUser();

  @Override
  public abstract S3Package getS3Package();

  @Override
  public String getDownloadingCommand(String fromUrl, String toLocation) {
    return String.format("wget %s -O %s", fromUrl, toLocation);
  }

  @Override
  public String getInstallingCommand(String packagePath) {
    return String.format("sudo dpkg -i %s", packagePath);
  }

  @Override
  public List<String> getDockerInstallingCommands() {
    return Arrays.asList(
        "sudo snap install docker"
    );
  }
}
