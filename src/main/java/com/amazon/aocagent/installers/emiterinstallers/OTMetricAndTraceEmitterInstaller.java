package com.amazon.aocagent.installers.emiterinstallers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.models.Context;

import java.util.Arrays;

public class OTMetricAndTraceEmitterInstaller implements OTEmitterInstaller {
  Context context;
  SSHHelper sshHelper;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;

    // init sshHelper
    this.sshHelper =
        new SSHHelper(
            this.context.getTestingAMI().getLoginUser(),
            this.context.getInstancePublicIpAddress(),
            GenericConstants.SSH_CERT_LOCAL_PATH.getVal());
  }

  @Override
  public void installAndStart() throws Exception {
    String dockerCommand =
        String.format(
            "sudo docker run -d --env otlp_endpoint=172.17.0.1:55680 --env otlp_instance_id=%s %s",
            context.getInstanceID(), GenericConstants.METRIC_EMITTER_DOCKER_IMAGE_URL.getVal());

    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(dockerCommand));
        });
  }
}
