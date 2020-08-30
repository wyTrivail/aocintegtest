package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

import java.util.Arrays;
import java.util.List;

public class SuseAMI extends LinuxAMI {
  public SuseAMI(String amiId) {
    super(amiId);
  }

  @Override
  public String getLoginUser() {
    return "ec2-user";
  }

  @Override
  public List<String> getDockerInstallingCommands() {
    return Arrays.asList("sudo service docker start");
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.SUSE_AMD64_RPM;
  }
}
