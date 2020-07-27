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
   * @param region the region for CloudWatch
   */
  public CloudWatchService(String region) {
    amazonCloudWatch = AmazonCloudWatchClientBuilder.standard().withRegion(region).build();
  }

  /**
   * listMetrics fetches metrics from CloudWatch.
   * todo we will add more methods to support multi dimension filters.
   * @param nameSpace the metric namespace on CloudWatch
   * @param dimensionFilterName the dimension name on CloudWatch
   * @param dimensionFilterValue the dimension value on CloudWatch
   * @return List of Metrics
   */
  public List<Metric> listMetrics(
      final String nameSpace, final String dimensionFilterName, final String dimensionFilterValue) {
    final ListMetricsRequest listMetricsRequest =
        new ListMetricsRequest()
            .withNamespace(nameSpace)
            .withDimensions(
                new DimensionFilter()
                    .withName(dimensionFilterName)
                    .withValue(dimensionFilterValue));
    return amazonCloudWatch.listMetrics(listMetricsRequest).getMetrics();
  }
}
