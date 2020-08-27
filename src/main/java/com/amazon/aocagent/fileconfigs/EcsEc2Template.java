package com.amazon.aocagent.fileconfigs;

import lombok.Getter;

@Getter
public enum EcsEc2Template implements FileConfig {
  ECS_EC2_TEMPLATE("/templates/ecs/aoc-sidecar-ec2.mustache"),
  ;

  private String path;

  EcsEc2Template(String path) {
    this.path = path;
  }
}
