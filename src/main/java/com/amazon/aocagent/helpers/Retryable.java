package com.amazon.aocagent.helpers;

public interface Retryable {
  void execute() throws Exception;
}
