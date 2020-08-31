package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class AmazonLinuxAMI extends LinuxAMI {
  public AmazonLinuxAMI(String amiId) {
    super(amiId);
  }

  @Override
  public String getLoginUser() {
    return "ec2-user";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.AMAZON_LINUX_AMD64_RPM;
  }
}
