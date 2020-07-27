package com.amazon.aocagent.exception;

public enum ExceptionCode {
  LOCAL_PACKAGE_NOT_EXIST(20000, "local package not exist"),
  S3_KEY_ALREADY_EXIST(20001, "s3 key is existed already"),
  SSH_COMMAND_FAILED(20002, "ssh command failed"),
  LOGIN_USER_NOT_FOUND(20003, "login user not found"),
  FAILED_AFTER_RETRY(20004, "failed after retry"),
  EC2INSTANCE_STATUS_PENDING(20005, "ec2 instance status is pending"),
  EC2INSTANCE_STATUS_BAD(20006, "ec2 instance status is bad"),
  NO_MATCHED_S3_KEY(20007, "no matched s3 key for this ami"),
  NO_MATCHED_DOWNLOADING_COMMAND(20008, "no matched downloading command for this ami"),
  NO_MATCHED_PACKAGE_NAME(20009, "no matched package name for this ami"),
  NO_MATCHED_INSTALLING_COMMAND(20010, "no matched installing command for this ami"),
  NO_MATCHED_STARTING_COMMAND(20010, "no matched starting command for this ami"),
  NO_MATCHED_DOCKER_INSTALLING_COMMAND(20010, "no matched docker installing command for this ami"),

  EXPECTED_METRIC_NOT_FOUND(30001, "expected metric not found"),

  ;
  private int code;
  private String message;

  ExceptionCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
