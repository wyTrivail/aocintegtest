package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class Ubuntu extends DebianAMI {
  @Override
  public String getAMIId() {
    return "ami-003634241a8fcdec0";
  }

  @Override
  public String getLoginUser() {
    return "ubuntu";
  }

  @Override
  public String getS3Key(String version) {
    return S3Package.UBUNTU_AMD64_DEB.getS3Key(version);
  }

  @Override
  public String getPackageName() {
    return S3Package.UBUNTU_AMD64_DEB.getPackageName();
  }
}
