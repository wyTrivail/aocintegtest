package com.amazon.aocagent.exception;

import lombok.Getter;

@Getter
public class BaseException extends Exception {
  private int code;
  private String message;

  public BaseException(ExceptionCode exceptionCode) {
    this.code = exceptionCode.getCode();
    this.message = exceptionCode.getMessage();
  }

  public BaseException(ExceptionCode exceptionCode, String message) {
    this.code = exceptionCode.getCode();
    this.message = message;
  }
}
