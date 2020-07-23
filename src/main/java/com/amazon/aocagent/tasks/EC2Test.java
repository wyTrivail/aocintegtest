package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.helpers.OTInstallHelper;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazonaws.services.ec2.model.Instance;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;

@Log4j2
public class EC2Test implements ITask {
  private EC2Service ec2Service;
  private Context context;

  @Override
  public void init(final Context context) throws Exception {
    ec2Service = new EC2Service(context.getRegion());
    this.context = context;
  }

  @Override
  public void execute() throws Exception {
    prepareSSHKey();

    // launch ec2 instance for testing
    Instance instance = ec2Service.launchInstance(context.getTestingAMI().getVal());

    // init sshHelper
    SSHHelper sshHelper = new SSHHelper(
        this.context.getTestingAMI().getLoginUser(),
        instance.getPublicIpAddress(),
        GenericConstants.SSH_CERT_LOCAL_PATH.getVal()
    );

    // wait until the instance is ready to login
    log.info("wait until the instance is ready to login");
    RetryHelper.retry(() -> {
      sshHelper.isSSHReady();
    });

    // Configure ot collector
    OTInstallHelper otInstallHelper = new OTInstallHelper(sshHelper, context);
    otInstallHelper.installAndStart();

    // validate

  }

  private void prepareSSHKey() throws IOException, BaseException {
    // create the ssh keypair if not existed.
    ec2Service.createSSHKeyIfNotExisted(GenericConstants.SSH_KEY_NAME.getVal());

    // download the ssh keypair from s3
    ec2Service.downloadSSHKey(
        GenericConstants.SSH_KEY_NAME.getVal(),
        GenericConstants.SSH_CERT_LOCAL_PATH.getVal());
  }

  @Override
  public String response() throws Exception {
    return null;
  }
}
