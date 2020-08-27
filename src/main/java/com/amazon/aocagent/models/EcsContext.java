package com.amazon.aocagent.models;

import lombok.Data;

@Data
public class EcsContext {

  /** ECS Service launch type. Eg, EC2 or Fargate. */
  private String launchType;

  /** ECS deployment mode. Eg, SIDECAR or DaemonSet. */
  private String deploymentMode;

  private String clusterName;

}
