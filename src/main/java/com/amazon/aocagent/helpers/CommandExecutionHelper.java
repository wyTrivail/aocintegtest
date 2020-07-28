package com.amazon.aocagent.helpers;

import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Log4j2
public class CommandExecutionHelper {
  /**
   * Amount of time to wait for processes to finish. Probably way more time than needed, but want to
   * pick a large value since the test will fail if the timeout is reached.
   */
  private static final long TIMEOUT_IN_SECONDS = 360;

  private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

  /**
   * Useful for sending the stdout/stderr of a child process to our logger. When started in its own
   * thread, the thread will terminate automatically when the inputStream is given EOF (such as when
   * a process connected to that stream is terminated).
   *
   * <p>Note that Java's Process object has some pretty confusing names, its 'getInputStream' and
   * 'getErrorStream' return java InputStream's for OUR process to consume from. But they are the
   * stdout/stderr of the child process represented by the Process object.
   */
  @RequiredArgsConstructor
  private static class StreamRedirecter implements Runnable {

    private final InputStream inputStream;
    private final Consumer<String> streamConsumer;

    public void run() {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
      reader.lines().forEach(streamConsumer);
    }
  }

  /**
   * runChildProcess executes the command in a child process.
   * @param command the command to be executed
   * @throws BaseException when the command fails to execute
   */
  public static void runChildProcess(String command) throws BaseException {
    log.info("execute command: {}", command);
    Process p;
    Future<?> stdoutFuture;
    Future<?> stderrFuture;

    try {
      p = Runtime.getRuntime().exec(command, new String[] {});

      // p is set up by default to just have pipes for its stdout/stderr, so those will buffer until
      // we set up consumers
      StreamRedirecter stdoutRedirector = new StreamRedirecter(p.getInputStream(), log::info);
      stdoutFuture = THREAD_POOL.submit(stdoutRedirector);

      StreamRedirecter stderrRedirector = new StreamRedirecter(p.getErrorStream(), log::error);
      stderrFuture = THREAD_POOL.submit(stderrRedirector);

    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }

    try {
      stdoutFuture.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
      stderrFuture.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
      if (0 != p.waitFor()) {
        throw new BaseException(ExceptionCode.COMMAND_FAILED_TO_EXECUTE);
      }
    } catch (InterruptedException | ExecutionException e) {
      p.destroyForcibly();
      throw new RuntimeException(e);
    } catch (TimeoutException e) {
      p.destroyForcibly();
      throw new RuntimeException("Timed out while waiting for command to complete.", e);
    }
  }
}
