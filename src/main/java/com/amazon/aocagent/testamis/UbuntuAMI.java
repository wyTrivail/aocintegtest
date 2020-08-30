package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class UbuntuAMI extends DebianAMI {
  public UbuntuAMI(String amiId) {
    super(amiId);
  }

  @Override
  public String getLoginUser() {
    return "ubuntu";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.DEBIAN_AMD64_DEB;
  }
}
