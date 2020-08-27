package com.amazon.aocagent.fileconfigs;

import lombok.Getter;

@Getter
public enum EcsFargateTemplate implements FileConfig {
  ECS_FARGATE_TEMPLATE("/templates/ecs/aoc-sidecar-fargate.mustache"),
  ;

  private String path;

  EcsFargateTemplate(String path) {
    this.path = path;
  }

}
