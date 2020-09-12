package com.amazon.aocagent.models;

import lombok.Data;

@Data
public class Stack {
  String testingRegion;
  String s3ReleaseCandidateBucketName;
  String s3BucketName;
  String sshKeyS3BucketName;
  String traceDataS3BucketName;
  String testingImageRepoName;
}
