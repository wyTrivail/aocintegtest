package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class A1SuseAMI extends SuseAMI {
  public A1SuseAMI(String amiId) {
    super(amiId);
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.SUSE_ARM64_RPM;
  }
}
