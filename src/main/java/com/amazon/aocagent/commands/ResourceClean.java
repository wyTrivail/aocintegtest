package com.amazon.aocagent.commands;

import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.tasks.TaskFactory;
import lombok.SneakyThrows;
import picocli.CommandLine;

@CommandLine.Command(
    name = "clean",
    mixinStandardHelpOptions = true,
    description = "use to clean resources of the aocintegtest")
public class ResourceClean implements Runnable {
  @CommandLine.Mixin CommonOption commonOption = new CommonOption();

  @CommandLine.Option(
      names = {"-t", "--clean-task"},
      description = "EC2Clean, ECSClean, EKSClean",
      defaultValue = "EC2Clean")
  private String cleanTask;

  @SneakyThrows
  @Override
  public void run() {
    Context context = commonOption.buildContext();
    TaskFactory.executeTask(cleanTask, context);
  }
}
