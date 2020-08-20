package com.amazon.aocagent.testamis;

import java.util.Arrays;
import java.util.List;

public abstract class SuseAMI extends LinuxAMI {

  @Override
  public String getLoginUser() {
    return "ec2-user";
  }

  @Override
  public List<String> getDockerInstallingCommands() {
    return Arrays.asList(
        "sudo zypper -n in docker",
        String.format("sudo usermod -aG docker %s", this.getLoginUser()),
        "sudo systemctl start docker");
  }
}
