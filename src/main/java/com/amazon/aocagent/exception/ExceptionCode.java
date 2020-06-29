package com.amazon.aocagent.exception;

public enum ExceptionCode {
  LOCAL_PACKAGE_NOT_EXIST(20000, "local package not exist"),
  S3_KEY_ALREADY_EXIST(20001, "s3 key is existed already"),
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
