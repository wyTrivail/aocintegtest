package com.amazon.aocagent.tasks;

import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazonaws.services.ec2.model.Instance;
import lombok.extern.log4j.Log4j2;

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
    Instance instance = ec2Service.launchInstance(context.getTestingAMI().getVal());

    log.info("got ec2 instance: {}", instance.getInstanceId());

    waitUntilInstanceAvailable(instance);
  }

  @Override
  public String response() throws Exception {
    return null;
  }

  private void waitUntilInstanceAvailable(Instance instance) throws Exception {
    log.info("wait until ec2 instance network ready");

    log.info("wait until ec2 instance is able to login");
    SSHHelper sshHelper = new SSHHelper(
        this.context.getTestingAMI().getLoginUser(),
        instance.getPublicIpAddress(),
        this.context.getSshCertPath()
    );

    RetryHelper.retry(() -> {
      sshHelper.isSSHReady();
    });
  }
}
