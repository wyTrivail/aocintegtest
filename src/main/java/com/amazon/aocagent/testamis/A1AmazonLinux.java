package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class A1AmazonLinux extends LinuxAMI {
  @Override
  public String getAMIId() {
    return "ami-091a6d6d0ed7b35fd";
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
