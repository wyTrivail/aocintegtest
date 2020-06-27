package com.amazon.aocagent.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum S3Package {
    AMAZON_LINUX_AMD64_RPM(SupportedOSDistribution.AMAZON_LINUX, Architecture.AMD64, PackageType.RPM, LocalPackage.LINUX_AMD64_RPM),
    AMAZON_LINUX_ARM64_RPM(SupportedOSDistribution.AMAZON_LINUX, Architecture.ARM64, PackageType.RPM, LocalPackage.LINUX_ARM64_RPM),
    ;

    private PackageType packageType;
    private Architecture architecture;
    private SupportedOSDistribution supportedOSDistribution;
    private LocalPackage localPackage;

    S3Package(SupportedOSDistribution supportedOSDistribution, Architecture architecture, PackageType packageType, LocalPackage localPackage){
        this.supportedOSDistribution = supportedOSDistribution;
        this.architecture = architecture;
        this.packageType = packageType;
        this.localPackage = localPackage;
    }

    public String getS3Key(String packageVersion){
        return String.join("/", Arrays.asList(
                supportedOSDistribution.name().toLowerCase(),
                architecture.name().toLowerCase(),
                packageVersion,
                GenericConstants.PACKAGE_NAME_PREFIX.getVal() + packageType.name().toLowerCase()
        ));
    }
}
