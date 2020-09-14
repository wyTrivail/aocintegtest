package com.amazon.aocagent.fileconfigs;

import lombok.Getter;

@Getter
public enum EksKubeConfigTemplate implements FileConfig {
  KUBE_CONFIG_TEMPLATE("/templates/eks/kubeConfig.mustache"),
  ;

  private String path;

  EksKubeConfigTemplate(String path) {
    this.path = path;
  }
}
