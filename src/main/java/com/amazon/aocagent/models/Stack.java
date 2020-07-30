package com.amazon.aocagent.models;

import lombok.Data;

@Data
public class Stack {
  String testingRegion;
  String s3TestingBucketName;
  String s3ReleaseCandidateBucketName;
  String s3BucketName;
  String sshKeyS3BucketName;

  public void switchS3BucketForTesting() {
    this.s3BucketName = this.s3TestingBucketName;
  }
}
