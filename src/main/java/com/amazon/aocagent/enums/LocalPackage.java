package com.amazon.aocagent.enums;

import java.util.Arrays;

public enum LocalPackage {
  LINUX_AMD64_RPM(OSType.LINUX, Architecture.AMD64, PackageType.RPM),
  LINUX_ARM64_RPM(OSType.LINUX, Architecture.ARM64, PackageType.RPM),
  ;

  private PackageType packageType;
  private Architecture architecture;
  private OSType osType;

  LocalPackage(OSType osType, Architecture architecture, PackageType packageType) {
    this.osType = osType;
    this.architecture = architecture;
    this.packageType = packageType;
  }

  /**
   * getFilePath generates the local path for the package.
   * @param localPackagesDir is used as the "root" directory of the package
   * @return the local path of the package
   */
  public String getFilePath(String localPackagesDir) {
    return String.join(
        "/",
        Arrays.asList(
            localPackagesDir,
            osType.name().toLowerCase(),
            architecture.name().toLowerCase(),
            GenericConstants.PACKAGE_NAME_PREFIX.getVal() + packageType.name().toLowerCase()));
  }
}
