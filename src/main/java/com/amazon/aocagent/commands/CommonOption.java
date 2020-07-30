package com.amazon.aocagent.commands;

import com.amazon.aocagent.enums.GenericConstants;
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

  @CommandLine.Option(
      names = {"--release"},
      description = "false means we will upload packages to the s3 testing bucket")
  private boolean release;

  /**
   * buildContext build the context object based on the command args.
   *
   * @return Context
   * @throws IOException when the VERSION file is not found
   */
  public Context buildContext() throws IOException {
    Context context = new Context();

    // build stack
    Stack stack = this.buildStack();
    if (!release) {
      log.info("use testing bucket for integ-test");
      stack.switchS3BucketForTesting();
    } else {
      log.info("attention!!! you are using the release bucket");
    }
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

  private Stack buildStack() throws IOException {
    // read stack from .aoc-stack
    if (!Files.exists(Paths.get(GenericConstants.STACK_FILE_PATH.getVal()))) {
      log.info("no .aoc-stack.yml, use the default stack");
      return buildDefaultStack();
    }

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readValue(
        new String(Files.readAllBytes(Paths.get(GenericConstants.STACK_FILE_PATH.getVal()))),
        Stack.class);
  }

  private Stack buildDefaultStack() {
    Stack stack = new Stack();
    stack.setTestingRegion(GenericConstants.DEFAULT_REGION.getVal());
    stack.setS3TestingBucketName(GenericConstants.DEFAULT_S3_TESTING_BUCKET.getVal());
    stack.setS3ReleaseCandidateBucketName(
        GenericConstants.DEFAULT_S3_RELEASE_CANDIDATE_BUCKET.getVal());
    stack.setS3BucketName(GenericConstants.DEFAULT_S3_BUCKET.getVal());
    stack.setSshKeyS3BucketName(GenericConstants.DEFAULT_SSH_KEY_S3_BUCKET_NAME.getVal());
    return stack;
  }
}
