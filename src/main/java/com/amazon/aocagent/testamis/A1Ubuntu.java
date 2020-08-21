package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class A1Ubuntu extends DebianAMI {
  @Override
  public String getAMIId() {
    return "ami-0c75fb2e6a6be38f6";
  }

  @Override
  public String getLoginUser() {
    return "ubuntu";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.DEBIAN_ARM64_DEB;
  }
}
