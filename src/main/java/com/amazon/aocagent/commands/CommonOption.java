package com.amazon.aocagent.commands;

import com.amazon.aocagent.enums.Stack;
import com.amazon.aocagent.models.Context;
import picocli.CommandLine;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@CommandLine.Command(footer = "Common footer")
public class CommonOption {
  @CommandLine.Option(
      names = {"-s", "--stack"},
      description = "TEST, RELEASE, HKG_RELEASE...",
      defaultValue = "TEST")
  private String stackName = "TEST";

  @CommandLine.Option(
      names = {"-l", "--local-packages-dir"},
      description =
          "read packages, version file from this directory, default value is build/packages",
      defaultValue = "build/packages")
  private String localPackagesDir;

  @CommandLine.Option(
      names = {"-r", "--region"},
      description =
          "region will be used to create the testing resource like EC2 Instance,"
              + " and be used to perform regionlized release, the default value is us-west-2",
      defaultValue = "us-west-2")
  private String region;

  /**
   * buildContext build the context object based on the command args.
   * @return Context
   * @throws IOException when the VERSION file is not found
   */
  public Context buildContext() throws IOException {
    Context context = new Context();

    Stack stack = Stack.valueOf(this.stackName);
    context.setStack(stack);

    context.setLocalPackagesDir(this.localPackagesDir);

    // get aoc version from the current working directory: "build/packages/VERSION"
    String version =
        new String(
                Files.readAllBytes(Paths.get(this.localPackagesDir + "/VERSION")),
                StandardCharsets.UTF_8)
            .trim();
    context.setAgentVersion(version);

    context.setRegion(this.region);

    return context;
  }
}
