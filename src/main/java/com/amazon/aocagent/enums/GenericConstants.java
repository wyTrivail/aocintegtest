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
  DEFAULT_TRACE_S3_BUCKET_NAME("trace-expected-data"),

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

  // emitter
  /* metric emitter
  The code to generate the below docker image has been taken from below repository
   * https://github.com/mxiamxia/aws-cloudwatch-opentelemetry-sample/tree/master/generator
   * */
  METRIC_EMITTER_DOCKER_IMAGE_URL("darwhs/aoc-metric-generator"),
  TRACE_EMITTER_ENDPOINT("http://localhost:4567/span0"),
  SERVICE_NAMESPACE("AWSObservability"),
  SERVICE_NAME("CloudWatchOTService"),
  TRACE_EMITTER_DOCKER_IMAGE_URL("josephwy/integ-test-emitter"),

  // validator related
  METRIC_NAMESPACE("default"),

  // release candidate related
  CANDIDATE_PACK_TO("build/candidate.tar.gz"),
  CANDIDATE_DOWNLOAD_TO("build/candidate-downloaded.tar.gz"),
  CANDIDATE_UNPACK_TO("."),

  //ECS
  ECS_LAUNCH_TYPE("ecsLaunchType"),
  ECS_DEPLOY_MODE("ecsDeployMode"),
  EC2_INSTANCE_ECS_TAG_VAL("aoc-integ-test-ecs"),
  ECS_SIDECAR_CLUSTER("aoc-sidecar-integ-test"),
  AOC_IMAGE("josephwy/awscollector:"),

  // common constants
  EC2("EC2"),
  FARGATE("FARGATE"),
  DEFAULT("default"),
  AOC_PREFIX("aoc-"),
  ;

  private String val;

  GenericConstants(String val) {
    this.val = val;
  }
}
