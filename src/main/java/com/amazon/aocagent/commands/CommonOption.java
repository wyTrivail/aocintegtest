package com.amazon.aocagent.commands;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.models.Stack;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@CommandLine.Command(footer = "Common footer")
@Log4j2
public class CommonOption {
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

  @CommandLine.Option(
      names = {"-p", "--package-version"},
      description = "the package version, fetched from local-packages-dir/VERSION by default")
  private String version;

  /**
   * buildContext build the context object based on the command args.
   *
   * @return Context
   * @throws IOException when the VERSION file is not found
   */
  public Context buildContext() throws IOException, BaseException {
    // build stack
    Stack stack = this.buildStack();

    Context context = new Context();

    context.setStack(stack);

    // local package dir
    context.setLocalPackagesDir(this.localPackagesDir);

    // get aoc version from the current working directory: "build/packages/VERSION"
    if (StringUtils.isNullOrEmpty(this.version)) {
      this.version =
          new String(
                  Files.readAllBytes(Paths.get(this.localPackagesDir + "/VERSION")),
                  StandardCharsets.UTF_8)
              .trim();
    }
    context.setAgentVersion(this.version);

    return context;
  }

  private Stack buildStack() throws IOException, BaseException {
    // read stack from .aoc-stack
    if (!Files.exists(Paths.get(GenericConstants.STACK_FILE_PATH.getVal()))) {
      throw new BaseException(ExceptionCode.STACK_FILE_NOT_FOUND);
    }

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readValue(
        new String(Files.readAllBytes(Paths.get(GenericConstants.STACK_FILE_PATH.getVal()))),
        Stack.class);
  }
}
