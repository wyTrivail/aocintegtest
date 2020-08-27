package com.amazon.aocagent.fileconfigs;

import lombok.Getter;

@Getter
public enum ExpectedMetric implements FileConfig {
  DEFAULT_EXPECTED_METRIC("/templates/validation/defaultExpectedMetric.mustache"),
  ;

  private String path;

  ExpectedMetric(String path) {
    this.path = path;
  }
}
