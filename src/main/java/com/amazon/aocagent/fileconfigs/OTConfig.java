package com.amazon.aocagent.fileconfigs;

import lombok.Getter;

@Getter
public enum OTConfig implements FileConfig {
  DEFAULT_OT_CONFIG("/templates/config/defaultOTConfig.mustache"),
  ;

  private String path;

  OTConfig(String path) {
    this.path = path;
  }
}
