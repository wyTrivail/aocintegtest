package com.amazon.aocagent.commands;

import com.amazon.aocagent.enums.TestCase;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.tasks.IntegTestFactory;
import com.amazon.aocagent.testamis.TestAMIFactory;
import lombok.SneakyThrows;
import picocli.CommandLine;

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
      names = {"-t", "--test-case"},
      description = "EC2Test, ECSTest, EKSTest",
      defaultValue = "EC2Test")
  private TestCase testCase;

  @SneakyThrows
  @Override
  public void run() {
    Context context = commonOption.buildContext();
    context.setTestingAMI(TestAMIFactory.getTestAMIFromName(testAMI));
    IntegTestFactory.runTestCase(testCase, context);
  }
}
