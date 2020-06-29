package com.amazon.aocagent.enums;

import lombok.Getter;

/**
 * each branch needs have its own testing stack in case of race condition. if you want to perform
 * integ-test on your own branch of AOC, don't forget to add a new testing stack here. each
 * regionalized release needs have its own release stack since the aws account will be different
 */
@Getter
public enum Stack {
  // Testing Stack
  TEST("aws-opentelemetry-collector-test"),

  // Release Stack, it's the production stack
  RELEASE("aws-opentelemetry-collector");

  private String s3BucketName;

  Stack(String s3BucketName) {
    this.s3BucketName = s3BucketName;
  }
}
