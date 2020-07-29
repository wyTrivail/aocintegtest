package com.amazon.aocagent.enums;

import lombok.Getter;

@Getter
public enum GenericConstants {

  DEFAULT_REGION("us-west-2"),

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
  SSH_KEY_S3_BUCKET("aoc-ssh-key"),
  SSH_KEY_NAME("aoc-ssh-key-2020-07-22"),
  SSH_CERT_LOCAL_PATH("/tmp/sshkey.pem"),
  SSH_TIMEOUT("30000"), //ms

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
  CANDIDATE_S3_BUCKET("aoc-candidate-packages"),
  CANDIDATE_PACK_TO("build/candidate.tar.gz"),
  CANDIDATE_DOWNLOAD_TO("build/candidate-downloaded.tar.gz"),
  CANDIDATE_UNPACK_TO("."),


  ;

  private String val;

  GenericConstants(String val) {
    this.val = val;
  }
}
