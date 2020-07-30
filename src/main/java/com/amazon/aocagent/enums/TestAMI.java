package com.amazon.aocagent.enums;

import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum TestAMI {
  AMAZON_LINUX2("ami-0e34e7b9ca0ace12d"),
  ;

  private String val;

  TestAMI(String val) {
    this.val = val;
  }

  /**
   * getLoginUser return the login username for base on the AMI.
   *
   * @return the login username
   * @throws BaseException when there's no loginuser configured for the AMI
   */
  public String getLoginUser() throws BaseException {
    switch (this) {
      case AMAZON_LINUX2:
        return "ec2-user";

      default:
        throw new BaseException(ExceptionCode.LOGIN_USER_NOT_FOUND);
    }
  }

  /**
   * getS3Key returns the s3 key for the ot package base on the AMI, which could be used to
   * construct the downloading link with the s3 domain.
   *
   * @param version the package version
   * @return the s3 key, without domain
   * @throws BaseException when no s3 key matched for the ami
   */
  public String getS3Key(String version) throws BaseException {
    switch (this) {
      case AMAZON_LINUX2:
        return S3Package.AMAZON_LINUX_AMD64_RPM.getS3Key(version);
      default:
        throw new BaseException(ExceptionCode.NO_MATCHED_S3_KEY);
    }
  }

  /**
   * getDownloadingCommand returns the command to download the ot package.
   *
   * @param fromUrl the downloading url
   * @param toLocation the path to place the downloaded file
   * @return the downloading command
   * @throws BaseException when no downloading command matched for the ami
   */
  public String getDownloadingCommand(String fromUrl, String toLocation) throws BaseException {
    switch (this) {
      case AMAZON_LINUX2:
        return String.format("curl -L %s -o %s", fromUrl, toLocation);
      default:
        throw new BaseException(ExceptionCode.NO_MATCHED_DOWNLOADING_COMMAND);
    }
  }

  /**
   * getInstallingCommand returns the command to install the ot package base on ami.
   *
   * @param packagePath the ot package path on the disk
   * @return the installing command
   * @throws BaseException when no installing command matched for the ami
   */
  public String getInstallingCommand(String packagePath) throws BaseException {
    switch (this) {
      case AMAZON_LINUX2:
        return String.format("sudo rpm -Uvh %s", packagePath);
      default:
        throw new BaseException(ExceptionCode.NO_MATCHED_INSTALLING_COMMAND);
    }
  }

  /**
   * getStartingCommand returns the command to start ot collect base on ami.
   *
   * @param configPath the configuration file path
   * @return the starting command
   * @throws BaseException when no starting command matched for the ami
   */
  public String getStartingCommand(String configPath) throws BaseException {
    switch (this) {
      case AMAZON_LINUX2:
        return String.format(
            "sudo %s -c %s -a start",
            "/opt/aws/aws-opentelemetry-collector/bin/aws-opentelemetry-collector-ctl", configPath);
      default:
        throw new BaseException(ExceptionCode.NO_MATCHED_STARTING_COMMAND);
    }
  }

  /**
   * getDockerInstallingCommand returns the commands to install docker base on ami.
   *
   * @return the commands to install and start dockerd
   * @throws BaseException when no docker installing command matached for the ami
   */
  public List<String> getDockerInstallingCommand() throws BaseException {
    switch (this) {
      case AMAZON_LINUX2:
        return Arrays.asList(
            "sudo yum update -y",
            "sudo yum install -y docker",
            "sudo service docker start",
            "sudo usermod -a -G docker ec2-user");
      default:
        throw new BaseException(ExceptionCode.NO_MATCHED_DOCKER_INSTALLING_COMMAND);
    }
  }

  /**
   * getPackageName returns the package name of ot base on the ami.
   *
   * @return the package name
   * @throws BaseException when no package name matched for the ami
   */
  public String getPackageName() throws BaseException {
    switch (this) {
      case AMAZON_LINUX2:
        return S3Package.AMAZON_LINUX_AMD64_RPM.getPackageName();
      default:
        throw new BaseException(ExceptionCode.NO_MATCHED_PACKAGE_NAME);
    }
  }
}
