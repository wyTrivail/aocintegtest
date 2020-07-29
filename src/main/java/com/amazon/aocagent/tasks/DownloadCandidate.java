package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.CommandExecutionHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.S3Service;
import com.amazonaws.regions.Regions;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadCandidate implements ITask {
  S3Service s3Service;
  Context context;

  static String COMMAND_TO_UNPACK = "tar -zxvf %s -C %s";

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
        context.getGithubSha() + ".tar.gz",
        GenericConstants.CANDIDATE_DOWNLOAD_TO.getVal());

    // unpack the tarball into cwd since it already keeps the folder structure
    CommandExecutionHelper.runChildProcess(String.format(
        COMMAND_TO_UNPACK,
        GenericConstants.CANDIDATE_DOWNLOAD_TO.getVal(),
        GenericConstants.CANDIDATE_UNPACK_TO.getVal()
    ));

    // validate version
    String versionFromFile = new String(
        Files.readAllBytes(
            Paths.get(context.getLocalPackagesDir() + "/VERSION")))
        .trim();
    if (!context.getAgentVersion().equals(versionFromFile)) {
      throw new BaseException(ExceptionCode.VERSION_NOT_MATCHED,
          "version is not matched " + versionFromFile + ":" + context.getAgentVersion());
    }

    // validate github sha
    if (!context
        .getGithubSha()
        .equals(
            new String(
                    Files.readAllBytes(
                        Paths.get(context.getLocalPackagesDir() + "/GITHUB_SHA")))
                .trim())) {
      throw new BaseException(ExceptionCode.GITHUB_SHA_NOT_MATCHED);
    }
  }
}
