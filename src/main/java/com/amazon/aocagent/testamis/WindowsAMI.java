package com.amazon.aocagent.testamis;

import com.amazon.aocagent.enums.OSType;
import com.amazon.aocagent.enums.S3Package;
import com.amazonaws.services.ec2.model.InstanceType;

public class WindowsAMI implements ITestAMI {
    private String amiId;

    public WindowsAMI(String amiId) {
        this.amiId = amiId;
    }

    @Override
    public String getAMIId() {
        return this.amiId;
    }

    @Override
    public OSType getOSType() {
        return OSType.WINDOWS;
    }

    // getLoginUser() is not used in WindowsAMI, simply return null here
    @Override
    public String getLoginUser() {
        return null;
    }

    @Override
    public S3Package getS3Package() {
        return S3Package.WINDOWS_AMD64_MSI;
    }

    @Override
    public String getDownloadingCommand(String fromUrl, String toLocation) {
        return String.format("wget %s -outfile C:\\%s", fromUrl, toLocation);
    }

    @Override
    public String getInstallingCommand(String packagePath) {
        return String.format("msiexec /i C:\\%s", packagePath);
    }
    @Override
    public String getStartingCommand(String configPath) {
        return String.format(
                "& %s -ConfigLocation %s -Action start",
                "'C:\\Program Files\\Amazon\\AwsObservabilityCollector\\aws-observability-collector-ctl.ps1'", configPath);
    }

    @Override
    public InstanceType getInstanceType() {
        return InstanceType.T2Medium;
    }

    @Override
    public String getIptablesCommand() {
        return null;
    }
}
