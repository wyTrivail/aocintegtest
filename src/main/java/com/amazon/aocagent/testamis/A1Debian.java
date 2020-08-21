package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class A1Debian extends DebianAMI {
  @Override
  public String getAMIId() {
    return "ami-0bb8fb45872332e66";
  }

  @Override
  public String getLoginUser() {
    return "admin";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.DEBIAN_ARM64_DEB;
  }
}
