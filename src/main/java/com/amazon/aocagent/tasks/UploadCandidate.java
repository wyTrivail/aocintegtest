package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.CommandExecutionHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.S3Service;
import com.amazonaws.regions.Regions;

/**
 * upload the tested packages to s3 as release candidates, use git commit to distinguish the
 * packages.
 */
public class UploadCandidate implements ITask {
  S3Service s3Service;
  Context context;
  static String COMMAND_TO_PACK = "tar -czvf %s %s";

  @Override
  public void init(Context context) throws Exception {
    this.context = context;
    s3Service = new S3Service(Regions.US_EAST_1.getName());
  }

  @Override
  public void execute() throws Exception {
    // archive the candidate packages, build tarball from local-packages dir
    CommandExecutionHelper.runChildProcess(
        String.format(
            COMMAND_TO_PACK,
            GenericConstants.CANDIDATE_PACK_TO.getVal(),
            context.getLocalPackagesDir()));

    // upload the zip file to s3
    s3Service.uploadS3ObjectWithPrivateAccess(
        GenericConstants.CANDIDATE_PACK_TO.getVal(),
        context.getStack().getS3ReleaseCandidateBucketName(),
        context.getGithubSha() + ".tar.gz",
        false);
  }
}
