package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class A1UbuntuAMI extends UbuntuAMI {
  public A1UbuntuAMI(String amiId) {
    super(amiId);
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.UBUNTU_ARM64_DEB;
  }
}
