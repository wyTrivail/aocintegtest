package com.amazon.aocagent;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;

public class S3Service {
  private AmazonS3 amazonS3;
  private static final String ENV_TRACE_BUCKET = "TRACE_DATA_BUCKET";
  private static final String ENV_TRACE_S3_KEY = "TRACE_DATA_S3_KEY";

  public S3Service(String region){
    amazonS3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
  }

  public void uploadTraceData(Response traceData) throws JsonProcessingException {
    String bucketName = System.getenv(ENV_TRACE_BUCKET);
    String keyName = System.getenv(ENV_TRACE_S3_KEY);

    if(bucketName == null || bucketName.trim().equals("")){
      throw new RuntimeException("bucketName is empty");
    }

    if(keyName == null || keyName.trim().equals("")){
      throw new RuntimeException("keyName is empty");
    }

    this.uploadS3Object(traceData.toJson(), bucketName, keyName);

  }

  private void uploadS3Object(String data, String bucketName, String key){
    // create Bucket if not existed
    if (!amazonS3.doesBucketExistV2(bucketName)) {
      amazonS3.createBucket(bucketName);
    }

    amazonS3.putObject(bucketName, key, data);
  }
}
