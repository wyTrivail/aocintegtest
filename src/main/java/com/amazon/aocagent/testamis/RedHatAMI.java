package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class RedHatAMI extends LinuxAMI {
  public RedHatAMI(String amiId) {
    super(amiId);
  }

  @Override
  public String getLoginUser() {
    return "ec2-user";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.REDHAT_AMD64_RPM;
  }
}
