package com.amazon.aocagent.testbeds;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazonaws.services.ec2.model.Instance;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class EC2TestBed implements TestBed {
  private EC2Service ec2Service;

  private Context context;

  @Override
  public void init(Context context) {
    this.context = context;
    this.ec2Service = new EC2Service(context.getStack().getTestingRegion());
  }

  @Override
  public Context launchTestBed() throws Exception {
    prepareSSHKey(context);

    ec2Service = new EC2Service(context.getStack().getTestingRegion());

    // launch ec2 instance for testing
    Instance instance = ec2Service.launchInstance(context.getTestingAMI());

    // init sshHelper
    SSHHelper sshHelper =
        new SSHHelper(
            context.getTestingAMI().getLoginUser(),
            instance.getPublicIpAddress(),
            GenericConstants.SSH_CERT_LOCAL_PATH.getVal());

    // wait until the instance is ready to login
    log.info("wait until the instance is ready to login");
    RetryHelper.retry(
        () -> {
          sshHelper.isSSHReady();
        });

    // install docker
    RetryHelper.retry(() -> installDocker(sshHelper));

    // setup instance id and publicAddress into context
    context.setInstanceId(instance.getInstanceId());
    context.setInstancePublicIpAddress(instance.getPublicIpAddress());
    return context;
  }

  private void prepareSSHKey(final Context context) throws BaseException {
    // download the ssh keypair from s3
    ec2Service.downloadSSHKey(
        context.getStack().getSshKeyS3BucketName(),
        GenericConstants.SSH_KEY_NAME.getVal(),
        GenericConstants.SSH_CERT_LOCAL_PATH.getVal());

    // change its permission to 400
    /*
    CommandExecutionHelper.runChildProcess(String.format(
        "chmod 400 %s",
        GenericConstants.SSH_CERT_LOCAL_PATH.getVal()
    ));
    */
  }

  private void installDocker(SSHHelper sshHelper) throws Exception {
    try {
      List<String> installingCommands = context.getTestingAMI().getDockerInstallingCommands();
      sshHelper.executeCommands(installingCommands);
    } catch (BaseException ex) {
      if (ExceptionCode.NO_MATCHED_DOCKER_INSTALLING_COMMAND.getCode() != ex.getCode()) {
        throw ex;
      }
    }
  }
}
