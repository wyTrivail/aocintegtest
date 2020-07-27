package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazon.aocagent.services.IAMService;
import java.io.IOException;

public class Setup implements ITask {
  EC2Service ec2Service;
  Context context;

  @Override
  public void init(Context context) throws Exception {
    ec2Service = new EC2Service(context.getRegion());
    this.context = context;
  }

  @Override
  public void execute() throws Exception {
    setupEC2RelatedResources();
  }

  private void setupEC2RelatedResources() throws IOException, BaseException {
    createIAMRole();
  }

  private void setupS3RelatedResources() {}

  private void createIAMRole() {
    IAMService iamService = new IAMService(context.getRegion());
    iamService.createIAMRoleIfNotExisted(GenericConstants.IAM_ROLE_NAME.getVal());
  }

  private void createS3ReleaseTestingBucket() {}

  private void createS3ReleaseBucket() {}
}
