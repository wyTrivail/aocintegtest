package com.amazon.aocagent;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.LongCounter;
import io.opentelemetry.metrics.Meter;

public class MetricEmitter {
  static final String DIMENSION_API_NAME = "apiName";
  static final String DIMENSION_STATUS_CODE = "statusCode";

  LongCounter returnTimeCounter;

  public MetricEmitter(String otlpEndpoint){
    Meter meter = OpenTelemetry.getMeter("cloudwatch-otel", "1.0");

    // give a instanceId appending to the metricname so that we can check the metric for each round of integ-test
    String metricName = "latency";
    String instanceId = System.getenv("INSTANCE_ID");
    if(instanceId != null && !instanceId.trim().equals("")){
      metricName += "_" + instanceId;
    }
    returnTimeCounter = meter
        .longCounterBuilder(metricName)
        .setDescription("API return time")
        .setUnit("ms")
        .build();
  }

  public void emitReturnTimeMetric(Long returnTime, String apiName, String statusCode){
    System.out.println("emit metric with returntime " + returnTime + "," + apiName + "," + statusCode);
    returnTimeCounter.add(returnTime, Labels.of(DIMENSION_API_NAME, apiName, DIMENSION_STATUS_CODE, statusCode));
  }
}
