package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class A1AmazonLinuxAMI extends AmazonLinuxAMI {
  public A1AmazonLinuxAMI(String amiId) {
    super(amiId);
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.AMAZON_LINUX_ARM64_RPM;
  }
}
