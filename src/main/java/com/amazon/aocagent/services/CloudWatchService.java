package com.amazon.aocagent.services;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.DimensionFilter;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.Metric;

import java.util.List;

/** a wrapper of cloudwatch client. */
public class CloudWatchService {
  AmazonCloudWatch amazonCloudWatch;

  /**
   * Construct CloudWatch Service with region.
   *
   * @param region the region for CloudWatch
   */
  public CloudWatchService(String region) {
    amazonCloudWatch = AmazonCloudWatchClientBuilder.standard().withRegion(region).build();
  }

  /**
   * listMetrics fetches metrics from CloudWatch.
   * @param nameSpace the metric namespace on CloudWatch
   * @param metricName the metric name on CloudWatch
   * @return List of Metrics
   */
  public List<Metric> listMetrics(
      final String nameSpace,
      final String metricName) {
    final ListMetricsRequest listMetricsRequest =
        new ListMetricsRequest()
            .withNamespace(nameSpace)
            .withMetricName(metricName);
    return amazonCloudWatch.listMetrics(listMetricsRequest).getMetrics();
  }
}
