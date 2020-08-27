package com.amazon.aocagent.installers.emiterinstallers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.models.TraceFromSDK;
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
            "sudo docker run --network host "
                + "-e OTEL_RESOURCE_ATTRIBUTES="
                + "service.namespace=%s,service.name=%s -e INSTANCE_ID=%s -d %s",
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
          String xrayTraceId = sshHelper.executeCommands(Arrays.asList(curlCommand));
          context.setExpectedTraceId(xrayTraceId);
        });

    // try twice at this moment, todo, remove this try after auto-instrument fix the first request
    // problem.
    TimeUnit.SECONDS.sleep(5);
    String traceData = sshHelper.executeCommands(Arrays.asList(curlCommand));

    // load metrics from yaml
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    TraceFromSDK traceFromSDK =
        mapper.readValue(traceData.getBytes(StandardCharsets.UTF_8),
            new TypeReference<TraceFromSDK>() {});

    context.setExpectedTraceId(traceFromSDK.getTraceId());
    context.setExpectedSpanIdList(traceFromSDK.getSpanIdList());
  }
}
