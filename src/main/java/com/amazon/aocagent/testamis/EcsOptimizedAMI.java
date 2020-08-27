package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class EcsOptimizedAMI extends LinuxAMI {

  @Override
  public String getAMIId() {
    return "ami-004e1655142a7ea0d"; // us-west-2
  }

  @Override
  public String getLoginUser() {
    return null;
  }

  @Override
  public S3Package getS3Package() {
    return null;
  }
}
