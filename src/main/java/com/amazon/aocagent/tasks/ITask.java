package com.amazon.aocagent.tasks;

import com.amazon.aocagent.models.Context;

public interface ITask {
  void init(final Context context) throws Exception;

  void execute() throws Exception;
}
