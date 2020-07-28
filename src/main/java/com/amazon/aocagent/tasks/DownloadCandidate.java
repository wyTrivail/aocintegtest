package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.S3Service;
import com.amazonaws.regions.Regions;
import org.zeroturnaround.zip.ZipUtil;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadCandidate implements ITask {
  S3Service s3Service;
  Context context;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;
    this.s3Service = new S3Service(Regions.US_EAST_1.getName());
  }

  @Override
  public void execute() throws Exception {
    // download candidate package from s3
    s3Service.downloadS3Object(
        GenericConstants.CANDIDATE_S3_BUCKET.getVal(),
        context.getGithubSha() + ".zip",
        GenericConstants.CANDIDATE_DOWNLOAD_TO.getVal());

    // unpack
    ZipUtil.unpack(
        new File(GenericConstants.CANDIDATE_DOWNLOAD_TO.getVal()),
        new File(GenericConstants.CANDIDATE_UNPACK_TO.getVal()));

    // validate version
    if (!context
        .getAgentVersion()
        .equals(
            new String(
                    Files.readAllBytes(
                        Paths.get(GenericConstants.CANDIDATE_UNPACK_TO.getVal() + "/VERSION")))
                .trim())) {
      throw new BaseException(ExceptionCode.VERSION_NOT_MATCHED);
    }

    // validate github sha
    if (!context
        .getGithubSha()
        .equals(
            new String(
                    Files.readAllBytes(
                        Paths.get(GenericConstants.CANDIDATE_UNPACK_TO.getVal() + "/GITHUB_SHA")))
                .trim())) {
      throw new BaseException(ExceptionCode.GITHUB_SHA_NOT_MATCHED);
    }
  }
}
