package com.amazon.aocagent.fileconfigs;

import lombok.Getter;

@Getter
public enum EksSidecarManifestTemplate implements FileConfig {
  MANIFEST_TEMPLATE("/templates/eks/aoc-eks-sidercar.mustache"),
  ;

  private String path;

  EksSidecarManifestTemplate(String path) {
    this.path = path;
  }
}
