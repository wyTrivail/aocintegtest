package com.amazon.aocagent.helpers;

import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.tasks.ITask;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TaskExecutionHelper {
  /**
   * executeTask inits and executes the task base on its name.
   * @param taskName the task name
   * @param context the context object
   * @throws Exception when a task throw an Exception
   */
  public static void executeTask(String taskName, Context context) throws Exception {
    ITask task = (ITask) Class.forName("com.amazon.aocagent.tasks." + taskName).newInstance();
    task.init(context);
    task.execute();
  }
}
