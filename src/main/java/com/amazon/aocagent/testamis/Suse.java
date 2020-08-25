package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class Suse extends SuseAMI {

  @Override
  public String getAMIId() {
    return "ami-063c2d222d223d0e9";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.SUSE_AMD64_RPM;
  }
}
