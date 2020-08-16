package com.amazon.aocagent.enums;

import lombok.Getter;

@Getter
public enum ExpectedMetric {
  EC2_EXPECTED_METRIC("EC2ExpectedMetric"),
  ;

  private String val;

  ExpectedMetric(String val) {
    this.val = val;
  }
}
