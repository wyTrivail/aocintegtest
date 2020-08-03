package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class AmazonLinux extends LinuxAMII {
  @Override
  public String getAMIId() {
    return "ami-0e34e7b9ca0ace12d";
  }

  @Override
  public String getLoginUser() {
    return "ec2-user";
  }

  @Override
  public String getS3Key(String version) {
    return S3Package.AMAZON_LINUX_AMD64_RPM.getS3Key(version);
  }

  @Override
  public String getPackageName() {
    return S3Package.AMAZON_LINUX_AMD64_RPM.getPackageName();
  }
}
