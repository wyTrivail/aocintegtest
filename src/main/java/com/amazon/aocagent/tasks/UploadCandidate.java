package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.S3Service;
import com.amazonaws.regions.Regions;
import org.zeroturnaround.zip.ZipUtil;
import java.io.File;

/**
 * upload the tested packages to s3 as release candidates, use git commit to distinguish the
 * packages.
 */
public class UploadCandidate implements ITask {
  S3Service s3Service;
  Context context;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;
    s3Service = new S3Service(Regions.US_EAST_1.getName());
  }

  @Override
  public void execute() throws Exception {
    // archive the candidate packages
    ZipUtil.pack(
        new File(context.getLocalPackagesDir()),
        new File(GenericConstants.CANDIDATE_PACK_TO.getVal()));

    // upload the zip file to s3
    s3Service.uploadS3ObjectWithPrivateAccess(
        GenericConstants.CANDIDATE_PACK_TO.getVal(),
        GenericConstants.CANDIDATE_S3_BUCKET.getVal(),
        context.getGithubSha() + ".zip",
        false);
  }
}
