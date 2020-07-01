package com.amazon.aocagent.exception;

public enum ExceptionCode {
  LOCAL_PACKAGE_NOT_EXIST(20000, "local package not exist"),
  S3_KEY_ALREADY_EXIST(20001, "s3 key is existed already"),
  SSH_COMMAND_FAILED(20002, "ssh command failed"),
  LOGIN_USER_NOT_FOUND(20003, "login user not found"),
  FAILED_AFTER_RETRY(20004, "failed after retry"),
  EC2INSTANCE_STATUS_PENDING(20005, "ec2 instance status is pending"),
  EC2INSTANCE_STATUS_BAD(20006, "ec2 instance status is bad"),
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
