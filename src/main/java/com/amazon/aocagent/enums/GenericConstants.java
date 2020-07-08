package com.amazon.aocagent.enums;

import lombok.Getter;

@Getter
public enum GenericConstants {

  DEFAULT_REGION("us-west-2"),

  // release related
  PACKAGE_NAME_PREFIX("aws-opentelemetry-collector."),
  LOCAL_PACKAGES_DIR("build/packages"),

  // ec2 related
  EC2_INSTANCE_TAG_KEY("aoc-integ-test-tag"),
  EC2_INSTANCE_TAG_VAL("aoc-integ-test"),

  // ssh related
  SSH_KEY_NAME("cwagent-test-2017-06-07"),
  SSH_CERT_DEFAULT_PATH("build/packages/sshkey.pem"),
  SSH_TIMEOUT("30000"), //ms

  // retry
  SLEEP_IN_MILLISECONDS("10000"), // ms
  MAX_RETRIES("10"),

  // task
  TASK_RESPONSE_FILE_LOCATION("./task_response")


  ;

  private String val;

  GenericConstants(String val) {
    this.val = val;
  }
}
