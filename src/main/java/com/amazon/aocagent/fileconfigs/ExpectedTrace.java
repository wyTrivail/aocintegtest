package com.amazon.aocagent.fileconfigs;

import com.amazon.aocagent.fileconfigs.FileConfig;
import lombok.Getter;

@Getter
public enum ExpectedTrace implements FileConfig {
  DEFAULT_EXPECTED_TRACE("/templates/validation/defaultExpectedTrace.mustache"),
  ;

  private String path;

  ExpectedTrace(String path) {
    this.path = path;
  }
}
