package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.LocalPackage;
import com.amazon.aocagent.enums.S3Package;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.S3Service;
import com.amazonaws.regions.Regions;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * S3Release will upload the packages[rpm, deb, msi, pkg] into the configured S3 bucket. it will be
 * used for AOC distribution as well as the pre-flight step in Integ-test to simulate the releases
 * in the Testing Stack
 */
public class S3Release implements ITask {
  private Context context;
  private S3Service s3Service;
  private String s3Bucket;

  @Override
  public void init(final Context context) throws Exception {
    this.context = context;

    // the global bucket is in us-east-1
    s3Service = new S3Service(Regions.US_EAST_1);
    // bucket name is globally unique, so we use different bucket name for different stacks
    s3Bucket = context.getStack().getS3BucketName();
  }

  @Override
  public void execute() throws Exception {
    this.releasePackagesToS3();
  }

  @Override
  public String response() throws Exception {
    return "success";
  }

  /**
   * releasePackagesToS3 upload all the packages to the S3 bucket.
   *
   * @throws BaseException when one of packages is not existed locally
   */
  public void releasePackagesToS3() throws BaseException {
    // validate if local packages are existed
    validateLocalPackage();

    // upload local packages to s3 with versioned key
    uploadToS3(context.getAgentVersion(), false);

    // upload local packages to s3 with the "latest" key, override the key if it's existed
    uploadToS3("latest", true);
  }

  private void validateLocalPackage() throws BaseException {
    for (LocalPackage builtPackage : LocalPackage.values()) {
      // assuming the local directory is os/arch/version/
      String filePath = builtPackage.getFilePath(context.getLocalPackagesDir());
      if (!Files.exists(Paths.get(filePath))) {
        throw new BaseException(
            ExceptionCode.LOCAL_PACKAGE_NOT_EXIST, "local package not exist: " + filePath);
      }
    }
  }

  private void uploadToS3(String packageVersion, boolean override) throws BaseException {
    for (S3Package s3Package : S3Package.values()) {
      s3Service.uploadS3Object(
          s3Package.getLocalPackage().getFilePath(context.getLocalPackagesDir()),
          s3Bucket,
          s3Package.getS3Key(packageVersion),
          override);
    }
  }
}
