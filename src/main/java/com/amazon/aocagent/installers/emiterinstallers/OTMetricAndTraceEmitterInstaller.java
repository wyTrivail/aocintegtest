package com.amazon.aocagent.installers.emiterinstallers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.models.TraceFromEmitter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
    // use host mode to interact with imds
    String dockerCommand =
        String.format(
            "sudo docker run --network host -e S3_REGION=%s -e TRACE_DATA_BUCKET=%s -e TRACE_DATA_S3_KEY=%s -e OTEL_RESOURCE_ATTRIBUTES=service.namespace=%s,service.name=%s -e INSTANCE_ID=%s -d %s",
            context.getStack().getTestingRegion(),
            context.getStack().getTraceDataS3BucketName(),
            context.getInstanceId(), // use instanceid as the s3 key of trace data
            GenericConstants.SERVICE_NAMESPACE.getVal(),
            GenericConstants.SERVICE_NAME.getVal(),
            context.getInstanceId(),
            GenericConstants.TRACE_EMITTER_DOCKER_IMAGE_URL.getVal());

    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(dockerCommand));
        });

    // wait until the trace emitter is ready to curl
    String curlCommand = String.format("curl %s", GenericConstants.TRACE_EMITTER_ENDPOINT.getVal());
    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(curlCommand));
        });

  }
}
