package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class AmazonLinux extends LinuxAMI {
  @Override
  public String getAMIId() {
    return "ami-0e34e7b9ca0ace12d";
  }

  @Override
  public String getLoginUser() {
    return "ec2-user";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.AMAZON_LINUX_ARM64_RPM;
  }
}
