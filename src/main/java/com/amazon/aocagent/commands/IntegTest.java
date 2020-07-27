package com.amazon.aocagent.commands;

import com.amazon.aocagent.enums.TestAMI;
import com.amazon.aocagent.helpers.TaskExecutionHelper;
import com.amazon.aocagent.models.Context;
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
      defaultValue = "AMAZON_LINUX2")
  private TestAMI testAMI;

  @CommandLine.Option(
      names = {"-t", "--test-case"},
      description = "EC2Test, ECSTest, EKSTest",
      defaultValue = "EC2Test")
  private String testCase;

  @SneakyThrows
  @Override
  public void run() {
    Context context = commonOption.buildContext();
    context.setTestingAMI(testAMI);

    TaskExecutionHelper.executeTask(testCase, context);
  }
}
