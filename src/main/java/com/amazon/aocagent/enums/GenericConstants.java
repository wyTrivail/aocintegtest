package com.amazon.aocagent.enums;

import lombok.Getter;

@Getter
public enum GenericConstants {
  PACKAGE_NAME_PREFIX("aws-opentelemetry-collector."),
  LOCAL_PACKAGES_DIR("build/packages"),
  ;

  private String val;

  GenericConstants(String val) {
    this.val = val;
  }
}
