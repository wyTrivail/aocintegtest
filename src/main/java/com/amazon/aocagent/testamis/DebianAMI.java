package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.S3Package;

import java.util.Arrays;
import java.util.List;

public class DebianAMI extends LinuxAMI {
  public DebianAMI(String amiId) {
    super(amiId);
  }

  @Override
  public String getLoginUser() {
    return "admin";
  }

  @Override
  public S3Package getS3Package() {
    return S3Package.DEBIAN_AMD64_DEB;
  }

  @Override
  public String getDownloadingCommand(String fromUrl, String toLocation) {
    return String.format("wget %s -O %s", fromUrl, toLocation);
  }

  @Override
  public String getInstallingCommand(String packagePath) {
    return String.format("sudo dpkg -i %s", packagePath);
  }
}
