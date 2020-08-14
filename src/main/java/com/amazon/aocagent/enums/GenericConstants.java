package com.amazon.aocagent.enums;

import lombok.Getter;

@Getter
public enum GenericConstants {

  // stack related
  DEFAULT_STACK_FILE_PATH(".aoc-stack.yml"),
  DEFAULT_REGION("us-west-2"),
  DEFAULT_S3_RELEASE_CANDIDATE_BUCKET("aoc-release-candidate"),
  DEFAULT_S3_BUCKET("aws-opentelemetry-collector-test"),
  DEFAULT_SSH_KEY_S3_BUCKET_NAME("aoc-ssh-key"),

  // release related
  PACKAGE_NAME_PREFIX("aws-opentelemetry-collector."),
  LOCAL_PACKAGES_DIR("build/packages"),
  GITHUB_SHA_FILE_NAME("GITHUB_SHA"),

  // ec2 related
  EC2_INSTANCE_TAG_KEY("aoc-integ-test-tag"),
  EC2_INSTANCE_TAG_VAL("aoc-integ-test"),
  DEFAULT_SECURITY_GROUP_NAME("default"),
  SECURITY_GROUP_NAME("aoc-integ-test-sp"),
  IAM_ROLE_NAME("aoc-integ-test-iam-role"),

  // ssh related
  SSH_KEY_NAME("aoc-ssh-key-2020-07-22"),
  SSH_CERT_LOCAL_PATH("sshkey.pem"),
  SSH_TIMEOUT("30000"), // ms

  // retry
  SLEEP_IN_MILLISECONDS("10000"), // ms
  MAX_RETRIES("10"),

  // task
  TASK_RESPONSE_FILE_LOCATION("./task_response"),

  // configuration
  EC2_CONFIG_PATH("/tmp/test.yml"),

  // metric emitter
  METRIC_EMITTER_DOCKER_IMAGE_URL("mxiamxia/aoc-metric-generator"),

  // validator related
  METRIC_NAMESPACE("default"),

  // release candidate related
  CANDIDATE_PACK_TO("build/candidate.tar.gz"),
  CANDIDATE_DOWNLOAD_TO("build/candidate-downloaded.tar.gz"),
  CANDIDATE_UNPACK_TO("."),

  //ECS
  LAUNCH_TYPE("launchType"),
  DEPLOY_MODE("deployMode"),
  ECS_IAM_ROLE_NAME("ecsInstanceRole"),
  ECS_EC2_AMI_ID("ami-004e1655142a7ea0d"), // us-west-2
  EC2_INSTANCE_ECS_TAG_VAL("aoc-integ-test-ecs"),
  ECS_SIDECAR_CLUSTER("aoc-sidecar-integ-test"),
  ECS_EC2_INSTANCE_ID("aoc-sidecar-ec2"),
  ECS_FARGATE_INSTANCE_ID("aoc-sidecar-fargate"),
  // To be edited under prod test account
  ECS_TASK_ROLE("arn:aws:iam::252610625673:role/CWAgentECSTaskRole"),
  ECS_TASK_EXECUTION_ROLE("arn:aws:iam::252610625673:role/CWAgentECSExecutionRole"),
  DATA_EMITTER_IMAGE("mxiamxia/aoc-metric-generator:latest"),
  AOC_IMAGE("mxiamxia/awscollector:v0.1.10"),
  ;

  private String val;

  GenericConstants(String val) {
    this.val = val;
  }
}
