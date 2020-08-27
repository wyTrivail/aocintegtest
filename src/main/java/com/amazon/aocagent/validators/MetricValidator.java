package com.amazon.aocagent.validators;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.CloudWatchService;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Log4j2
public class MetricValidator implements IValidator {
  private static int MAX_RETRY_COUNT = 60;

  private MustacheHelper mustacheHelper = new MustacheHelper();

  @Override
  public void validate(Context context) throws Exception {
    List<Metric> expectedMetricList = this.getExpectedMetricList(context);

    RetryHelper.retry(
        MAX_RETRY_COUNT,
        () -> {
          List<Metric> metricList = this.listMetricFromCloudWatch(context);
          log.info("Cloudwatch metric result set size: {}", metricList.size());

          // load metrics into a hash set
          Set<Metric> metricSet =
              new TreeSet<>(
                  (Metric o1, Metric o2) -> {
                    // check namespace
                    if (!o1.getNamespace().equals(o2.getNamespace())) {
                      return o1.getNamespace().compareTo(o2.getNamespace());
                    }

                    // check metric name
                    if (!o1.getMetricName().equals(o2.getMetricName())) {
                      return o1.getMetricName().compareTo(o2.getMetricName());
                    }

                    // sort and check dimensions
                    List<Dimension> dimensionList1 = o1.getDimensions();
                    List<Dimension> dimensionList2 = o2.getDimensions();
                    dimensionList1.sort(Comparator.comparing(Dimension::getName));
                    dimensionList2.sort(Comparator.comparing(Dimension::getName));
                    return dimensionList1.toString().compareTo(dimensionList2.toString());
                  });
          for (Metric metric : metricList) {
            metricSet.add(metric);
          }
          // check if all the expected metrics are in the metric list
          for (Metric metric : expectedMetricList) {
            if (!metricSet.contains(metric)) {
              throw new BaseException(
                  ExceptionCode.EXPECTED_METRIC_NOT_FOUND,
                  String.format(
                      "expected metric %s not found in metric from cloudwatch: %s",
                      metric, metricSet));
            }
          }

          // todo reverse check: check if all the metric in the metric list are in the expected
          // metrics
        });
  }

  private List<Metric> getExpectedMetricList(Context context) throws IOException {
    // get expected metrics as yaml from config
    String yamlExpectedMetrics = mustacheHelper.render(
            context.getExpectedMetric(), context);

    // load metrics from yaml
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    List<Metric> expectedMetricList =
        mapper.readValue(
            yamlExpectedMetrics.getBytes(StandardCharsets.UTF_8),
            new TypeReference<List<Metric>>() {});

    return expectedMetricList;
  }

  private List<Metric> listMetricFromCloudWatch(Context context) throws IOException {
    CloudWatchService cloudWatchService =
        new CloudWatchService(context.getStack().getTestingRegion());
    log.info("validating CW metrics on ns:{}, insId:{}",
            GenericConstants.METRIC_NAMESPACE.getVal(), context.getInstanceId());
    return cloudWatchService.listMetrics(
        GenericConstants.METRIC_NAMESPACE.getVal(), "instanceId", context.getInstanceId());
  }
}
