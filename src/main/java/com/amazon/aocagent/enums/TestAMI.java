package com.amazon.aocagent.enums;

import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import lombok.Getter;

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
}
