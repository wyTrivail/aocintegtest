package com.amazon.aocagent.testamis;

import java.util.Arrays;
import java.util.List;

public abstract class DebianAMI extends LinuxAMI {
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
    return String.format("wget %s -O %s", fromUrl, toLocation);
  }

  @Override
  public String getInstallingCommand(String packagePath) {
    return String.format("sudo dpkg -i %s", packagePath);
  }

  @Override
  public List<String> getDockerInstallingCommands() {
    // debian normally has docker by default
    return Arrays.asList("echo 'skip docker install on debian'");
  }
}
