package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class A1RedHatAMI extends RedHatAMI {
  public A1RedHatAMI(String amiId) {
    super(amiId);
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.REDHAT_ARM64_RPM;
  }
}
