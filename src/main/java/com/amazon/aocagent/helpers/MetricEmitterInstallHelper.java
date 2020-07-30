package com.amazon.aocagent.helpers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.models.Context;

import java.util.Arrays;

public class MetricEmitterInstallHelper {
  private SSHHelper sshHelper;
  private Context context;

  public MetricEmitterInstallHelper(SSHHelper sshHelper, Context context) {
    this.sshHelper = sshHelper;
    this.context = context;
  }

  /**
   * installAndStartEmitter start the emitter to emit metric and trace.
   *
   * @throws Exception when the emitter failed to start
   */
  public void installAndStartEmitter() throws Exception {
    String dockerCommand =
        String.format(
            "docker run -d --env otlp_endpoint=172.17.0.1:55680 --env otlp_instance_id=%s %s",
            context.getInstanceID(), GenericConstants.METRIC_EMITTER_DOCKER_IMAGE_URL.getVal());

    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(dockerCommand));
        });
  }
}
