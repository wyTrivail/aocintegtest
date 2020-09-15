package com.amazon.aocagent.tasks;

import com.amazon.aocagent.models.Context;

public class TaskFactory {
  /**
   * execute task base on name.
   *
   * @param taskName the task name
   * @param context the testing context
   * @throws Exception when the task execution fails
   */
  public static void executeTask(String taskName, Context context) throws Exception {
    ITask task =
        (ITask)
            Class.forName(TaskFactory.class.getPackage().getName() + "." + taskName).newInstance();
    try {
      task.init(context);
      task.execute();
    } finally {
      task.clean();
    }
  }
}
