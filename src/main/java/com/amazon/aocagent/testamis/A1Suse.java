package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class A1Suse extends SuseAMI {

  @Override
  public String getAMIId() {
    return "ami-0bfc92b18fd79372c";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.SUSE_ARM64_RPM;
  }
}
