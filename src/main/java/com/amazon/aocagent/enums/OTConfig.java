package com.amazon.aocagent.enums;

import lombok.Getter;

@Getter
public enum OTConfig {
  EC2_CONFIG("EC2Config"),
  ;

  private String val;

  OTConfig(String val) {
    this.val = val;
  }
}
