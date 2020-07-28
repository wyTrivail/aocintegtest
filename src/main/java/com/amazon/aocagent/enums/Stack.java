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
  TEST(
      "aws-opentelemetry-collector-test",
      "aoc-release-candidate"
      ),

  // Release Stack, it's the production stack,
  // let's use aws-opentelemetry-collector-release to test the release stack first
  // todo change the bucket name to aws-opentelemetry-collector for formal release
  RELEASE("aws-opentelemetry-collector-release",
      "");

  private String s3BucketName;
  private String s3ReleaseCandidateBucketName;

  Stack(String s3BucketName, String s3ReleaseCandidateBucketName) {
    this.s3BucketName = s3BucketName;
    this.s3ReleaseCandidateBucketName = s3ReleaseCandidateBucketName;
  }
}
