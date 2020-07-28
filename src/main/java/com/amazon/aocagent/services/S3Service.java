package com.amazon.aocagent.services;

import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import lombok.extern.log4j.Log4j2;

/** S3Service is the wrapper of Amazon S3 Client. */
@Log4j2
public class S3Service {
  private AmazonS3 amazonS3;

  public S3Service(String region) {
    amazonS3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
  }

  /**
   * uploadS3Object uploads the localfile to the s3 bucket.
   * @param localFilePath the path of local file to be
   * @param bucketName the s3 bucket name
   * @param key the s3 key name
   * @param override override the s3 key if it's already existed when override is true
   * @throws BaseException when the s3 key is already existed and override is false
   */
  public void uploadS3Object(String localFilePath, String bucketName, String key, boolean override)
      throws BaseException {
    this.uploadS3Object(
        localFilePath,
        bucketName,
        key,
        override,
        false,
        null,
        CannedAccessControlList.PublicRead);
  }

  private void uploadS3Object(
      String localFilePath,
      String bucketName,
      String key,
      boolean override,
      boolean exceptionOnKeyExisting,
      ObjectMetadata objectMetadata,
      CannedAccessControlList accessControlList)
      throws BaseException {
    // create Bucket if not existed
    if (!amazonS3.doesBucketExistV2(bucketName)) {
      amazonS3.createBucket(bucketName);
    }

    // check if the key is existed
    if (!override && amazonS3.doesObjectExist(bucketName, key)) {
      if (exceptionOnKeyExisting) {
        throw new BaseException(
            ExceptionCode.S3_KEY_ALREADY_EXIST, "s3 key is already existed: " + key);
      } else {
        log.warn("s3 key is already existed: {}, skip", key);
        return;
      }
    }

    PutObjectRequest putObjectRequest =
        new PutObjectRequest(bucketName, key, new File(localFilePath))
            .withCannedAcl(accessControlList);
    if (objectMetadata != null) {
      putObjectRequest.setMetadata(objectMetadata);
    }

    amazonS3.putObject(putObjectRequest);
  }

  /**
   * uploadS3ObjectWithPrivateAccess uploads the locafile to the s3 bucket with private access.
   * @param localFilePath the path of local file to be
   * @param bucketName the s3 bucket name
   * @param key the s3 key name
   * @param override override the s3 key if it's already existed when override is true
   * @throws BaseException when the s3 key is already existed and override is false
   */
  public void uploadS3ObjectWithPrivateAccess(String localFilePath,
                                              String bucketName,
                                              String key,
                                              boolean override) throws BaseException {
    this.uploadS3Object(
        localFilePath,
        bucketName,
        key, override,
        false,
        null,
        CannedAccessControlList.Private);
  }



  /**
   * downloadS3Object downloads the s3 object to local.
   * @param bucketName the s3 bucket name
   * @param key the s3 object key name
   * @param toLocation the local location to download to
   */
  public void downloadS3Object(String bucketName, String key, String toLocation) {
    log.info("download s3 object {}/{}", bucketName, key);
    amazonS3.getObject(
        new GetObjectRequest(bucketName, key),
        new File(toLocation));
  }

}
