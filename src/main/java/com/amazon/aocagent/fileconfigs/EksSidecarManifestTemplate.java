package com.amazon.aocagent.fileconfigs;

import lombok.Getter;

@Getter
public class EksSidecarManifestTemplate implements FileConfig {

  private String path;

  public EksSidecarManifestTemplate(String path) {
    this.path = path;
  }
}
