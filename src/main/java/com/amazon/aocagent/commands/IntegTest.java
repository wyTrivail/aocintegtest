package com.amazon.aocagent.commands;

import com.amazon.aocagent.enums.ExpectedMetric;
import com.amazon.aocagent.enums.OTConfig;
import com.amazon.aocagent.enums.TestCase;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.tasks.IntegTestFactory;
import com.amazon.aocagent.testamis.TestAMIFactory;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.Map;

@CommandLine.Command(
    name = "integ-test",
    mixinStandardHelpOptions = true,
    description = "used for the integtests of the aocagent")
public class IntegTest implements Runnable {
  @CommandLine.Mixin CommonOption commonOption = new CommonOption();

  @CommandLine.Option(
      names = {"-a", "--ami"},
      description = "Enum values: ${COMPLETION-CANDIDATES}, default: ${DEFAULT-VALUE}",
      defaultValue = "AmazonLinux")
  private String testAMI;

  @CommandLine.Option(
      names = {"-c", "--config"},
      description = "Enum values: ${COMPLETION-CANDIDATES}, default: ${DEFAULT-VALUE}",
      defaultValue = "EC2_CONFIG")
  private OTConfig otConfig;

  @CommandLine.Option(
      names = {"--expected-metric"},
      description = "Enum values: ${COMPLETION-CANDIDATES}, default: ${DEFAULT-VALUE}",
      defaultValue = "EC2_EXPECTED_METRIC")
  private ExpectedMetric expectedMetric;

  @CommandLine.Option(
      names = {"-t", "--test-case"},
      description = "EC2_TEST,ECS_SIDECAR",
      defaultValue = "EC2_TEST")
  private TestCase testCase;


  @SneakyThrows
  @Override
  public void run() {
    Context context = commonOption.buildContext();
    context.setTestingAMI(TestAMIFactory.getTestAMIFromName(testAMI));
    context.setOtConfig(otConfig);
    context.setExpectedMetric(expectedMetric);
    IntegTestFactory.runTestCase(testCase, context);
  }
}
