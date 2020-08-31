package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

public class CentosAMI extends LinuxAMI {
  public CentosAMI(String amiId) {
    super(amiId);
  }

  @Override
  public String getLoginUser() {
    return "centos";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.CENTOS_AMD64_RPM;
  }

}
