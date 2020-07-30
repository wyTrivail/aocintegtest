package com.amazon.aocagent.helpers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

@Log4j2
public class RetryHelper {
  /**
   * retry executes the lambda, retry if the lambda throw exceptions.
   *
   * @param retryCount the total retry count
   * @param sleepInMilliSeconds sleep time among retries
   * @param retryable the lambda
   * @throws Exception when the retry count is reached
   */
  public static void retry(int retryCount, int sleepInMilliSeconds, Retryable retryable)
      throws Exception {
    while (retryCount-- > 0) {
      try {
        log.info("still can retry for {} times", retryCount);
        retryable.execute();
        return;
      } catch (Exception ex) {
        log.error(ex.getMessage());
        TimeUnit.MILLISECONDS.sleep(sleepInMilliSeconds);
      }
    }

    throw new BaseException(ExceptionCode.FAILED_AFTER_RETRY);
  }

  /**
   * retry executes lambda with default retry count(10) and sleep seconds(10).
   *
   * @param retryable the lambda
   * @throws Exception when the retry count is reached
   */
  public static void retry(Retryable retryable) throws Exception {
    retry(
        Integer.valueOf(GenericConstants.MAX_RETRIES.getVal()),
        Integer.valueOf(GenericConstants.SLEEP_IN_MILLISECONDS.getVal()),
        retryable);
  }

  /**
   * retry executes lambda with default sleeping seconds 10s.
   *
   * @param retryCount the total retry count
   * @param retryable the lambda function to be executed
   * @throws Exception when the retry count is reached
   */
  public static void retry(int retryCount, Retryable retryable) throws Exception {
    retry(retryCount, Integer.valueOf(GenericConstants.SLEEP_IN_MILLISECONDS.getVal()), retryable);
  }
}
