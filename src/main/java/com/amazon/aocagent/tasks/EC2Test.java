package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.MetricEmitterInstallHelper;
import com.amazon.aocagent.helpers.OTInstallHelper;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazon.aocagent.services.IAMService;
import com.amazon.aocagent.validators.BatchedValidator;
import com.amazon.aocagent.validators.MetricValidator;
import com.amazonaws.services.ec2.model.Instance;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class EC2Test implements ITask {
  private EC2Service ec2Service;
  private Context context;

  @Override
  public void init(final Context context) throws Exception {
    ec2Service = new EC2Service(context.getStack().getTestingRegion());
    this.context = context;
  }

  @Override
  public void execute() throws Exception {
    prepareSSHKey();
    createIAMRole();

    // launch ec2 instance for testing
    Instance instance = ec2Service.launchInstance(context.getTestingAMI().getVal());

    // init sshHelper
    SSHHelper sshHelper =
        new SSHHelper(
            this.context.getTestingAMI().getLoginUser(),
            instance.getPublicIpAddress(),
            GenericConstants.SSH_CERT_LOCAL_PATH.getVal());

    // setup instance id into context
    context.setInstanceID(instance.getInstanceId());

    // wait until the instance is ready to login
    log.info("wait until the instance is ready to login");
    RetryHelper.retry(
        () -> {
          sshHelper.isSSHReady();
        });

    // Configure ot collector
    OTInstallHelper otInstallHelper = new OTInstallHelper(sshHelper, context);
    otInstallHelper.installAndStart();

    // Install docker to run metric emitter
    installDocker(sshHelper);

    // Configure metric emitter
    MetricEmitterInstallHelper metricEmitterInstallHelper =
        new MetricEmitterInstallHelper(sshHelper, context);
    metricEmitterInstallHelper.installAndStartEmitter();

    // validate
    BatchedValidator batchedValidator = new BatchedValidator(Arrays.asList(new MetricValidator()));
    batchedValidator.validate(context);
  }

  private void prepareSSHKey() throws IOException, BaseException {
    // download the ssh keypair from s3
    ec2Service.downloadSSHKey(
        this.context.getStack().getSshKeyS3BucketName(),
        GenericConstants.SSH_KEY_NAME.getVal(),
        GenericConstants.SSH_CERT_LOCAL_PATH.getVal());
  }

  private void installDocker(SSHHelper sshHelper) throws Exception {
    try {
      List<String> installingCommands = context.getTestingAMI().getDockerInstallingCommand();
      sshHelper.executeCommands(installingCommands);
    } catch (BaseException ex) {
      if (ExceptionCode.NO_MATCHED_DOCKER_INSTALLING_COMMAND.getCode() != ex.getCode()) {
        throw ex;
      }
    }
  }

  private void createIAMRole() {
    IAMService iamService = new IAMService(context.getStack().getTestingRegion());
    iamService.createIAMRoleIfNotExisted(GenericConstants.IAM_ROLE_NAME.getVal());
  }
}
