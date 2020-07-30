package com.amazon.aocagent.commands;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.TaskExecutionHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.models.Stack;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@CommandLine.Command(
    name = "setup",
    mixinStandardHelpOptions = true,
    description = "use for the stack setup of the aocintegtest")
@Log4j2
public class Setup implements Runnable {

  @CommandLine.Option(
      names = {"-r", "--testing-region"},
      description = "the testing region",
      defaultValue = "us-west-2")
  private String testingRegion;

  @SneakyThrows
  @Override
  public void run() {
    Stack stack = null;
    if (!Files.exists(Paths.get(GenericConstants.STACK_FILE_PATH.getVal()))) {
      log.info("no .aoc-stack.yml file, Build stack with timestamp");
      stack = this.buildStackWithTimestamp();
    } else {
      log.info("found .aoc-stack.yml file, Build stack from file");
      stack = this.buildStackFromFile();
    }

    Context context = new Context();
    context.setStack(stack);
    TaskExecutionHelper.executeTask("Setup", context);
  }

  private Stack buildStackWithTimestamp() {
    Stack stack = new Stack();
    String timestamp = Long.toHexString(System.currentTimeMillis());

    stack.setSshKeyS3BucketName(
        String.join("-", GenericConstants.DEFAULT_SSH_KEY_S3_BUCKET_NAME.getVal(), timestamp));

    stack.setS3ReleaseCandidateBucketName(
        String.join("-", GenericConstants.DEFAULT_S3_RELEASE_CANDIDATE_BUCKET.getVal(), timestamp));

    stack.setS3BucketName(String.join("-", GenericConstants.DEFAULT_S3_BUCKET.getVal(), timestamp));

    stack.setTestingRegion(this.testingRegion);

    return stack;
  }

  private Stack buildStackFromFile() throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readValue(
        new String(Files.readAllBytes(Paths.get(GenericConstants.STACK_FILE_PATH.getVal()))),
        Stack.class);
  }
}
