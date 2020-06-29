package com.amazon.aocagent.services;

import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;

/**
 * S3Service is the wrapper of Amazon S3 Client.
 */
public class S3Service {
  private AmazonS3 amazonS3;

  public S3Service(Regions region) {
    amazonS3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
  }

  public void uploadS3ObjectWithMetadata(
      String localFilePath,
      String bucketName,
      String key,
      boolean override,
      ObjectMetadata objectMetadata)
      throws BaseException {
    this.uploadS3Object(localFilePath, bucketName, key, override, objectMetadata);
  }

  public void uploadS3Object(String localFilePath, String bucketName, String key, boolean override)
      throws BaseException {
    this.uploadS3Object(localFilePath, bucketName, key, override, null);
  }



  private void uploadS3Object(
      String localFilePath,
      String bucketName,
      String key,
      boolean override,
      ObjectMetadata objectMetadata)
      throws BaseException {
    //create Bucket if not existed
    if (!amazonS3.doesBucketExistV2(bucketName)) {
      amazonS3.createBucket(bucketName);
    }

    // check if the key is existed
    if (!override && amazonS3.doesObjectExist(bucketName, key)) {
      throw new BaseException(
          ExceptionCode.S3_KEY_ALREADY_EXIST, "s3 key is already existed: " + key);
    }

    PutObjectRequest putObjectRequest =
        new PutObjectRequest(bucketName, key, new File(localFilePath))
            .withCannedAcl(CannedAccessControlList.PublicRead);
    if (objectMetadata != null) {
      putObjectRequest.setMetadata(objectMetadata);
    }

    amazonS3.putObject(putObjectRequest);
  }
}
