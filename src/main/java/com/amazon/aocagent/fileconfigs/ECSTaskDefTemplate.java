package com.amazon.aocagent.fileconfigs;

import lombok.Getter;

@Getter
public enum ECSTaskDefTemplate implements FileConfig {
  ECS_EC2_TEMPLATE("/templates/ecs/aoc-sidecar-ec2.mustache"),
  ECS_FARGATE_TEMPLATE("/templates/ecs/aoc-sidecar-fargate.mustache"),
  ;

  private String path;

  ECSTaskDefTemplate(String path) {
    this.path = path;
  }
}
