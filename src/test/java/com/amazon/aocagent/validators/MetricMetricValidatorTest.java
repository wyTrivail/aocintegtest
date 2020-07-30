package com.amazon.aocagent.validators;

import com.amazon.aocagent.models.Context;
import com.amazonaws.services.cloudwatch.model.Metric;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MetricMetricValidatorTest {

  @Test
  public void testGetExpectedMetricList()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Context context = new Context();
    context.setInstanceID("i-035a644c403f96199");

    Method method = MetricValidator.class.getDeclaredMethod("getExpectedMetricList", Context.class);
    method.setAccessible(true);
    List<Metric> metricList = (List<Metric>) method.invoke(new MetricValidator(), context);
    System.out.println(metricList);
  }
}
