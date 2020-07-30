package com.amazon.aocagent.commands;

import com.amazon.aocagent.helpers.TaskExecutionHelper;
import com.amazon.aocagent.models.Context;
import lombok.SneakyThrows;
import picocli.CommandLine;

@CommandLine.Command(
    name = "release",
    mixinStandardHelpOptions = true,
    description = "use for the release of the aocagent")
public class Release implements Runnable {
  @CommandLine.Mixin CommonOption commonOption = new CommonOption();

  @CommandLine.Option(
      names = {"-t", "--release-task"},
      description = "S3Release, ECRRelease, UploadCandidate, DownloadCandidate",
      defaultValue = "S3Release")
  private String releaseTask;

  @SneakyThrows
  @Override
  public void run() {
    Context context = commonOption.buildContext();

    TaskExecutionHelper.executeTask(releaseTask, context);
  }
}
