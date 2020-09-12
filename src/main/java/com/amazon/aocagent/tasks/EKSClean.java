package com.amazon.aocagent.tasks;

import com.amazon.aocagent.helpers.CommandExecutionHelper;
import com.amazon.aocagent.helpers.EKSTestOptionsValidationHelper;
import com.amazon.aocagent.models.Context;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
public class EKSClean implements ITask {
  private Context context;

  @Override
  public void init(Context context) throws Exception {
    EKSTestOptionsValidationHelper.checkEKSTestOptions(context);
    this.context = context;
  }

  @Override
  public void execute() throws Exception {
    String command =
        String.format(
            "%s get ns --kubeconfig %s", context.getKubectlPath(), context.getKubeconfigPath());
    String result = CommandExecutionHelper.runChildProcessWithAWSCred(command);

    List<String> namespaces = new ArrayList<>();
    String[] lines = result.split("\n");
    for (String line : lines) {
      if (line.startsWith("eks-integ-test-")) {
        // line example: "eks-integ-test-1599881334414   Active   10s"
        String namespace = line.split(" ")[0];
        // extract creation time (in milliseconds) of the namespace, i.e.
        // eks-integ-test-1599881334414
        String[] elements = namespace.split("-", 4);
        if (elements.length == 4) {
          String timestamp = elements[3];
          // add to target namespaces if it was created 2 hours ago
          if (new Date(Long.parseLong(timestamp)).before(new DateTime().minusHours(2).toDate())) {
            namespaces.add(namespace);
          }
        }
      }
    }

    log.info("Deleting old namespaces {}", namespaces);
    for (String namespace : namespaces) {
      command =
          String.format(
              "%s delete ns %s --kubeconfig %s",
              context.getKubectlPath(), namespace, context.getKubeconfigPath());
      CommandExecutionHelper.runChildProcessWithAWSCred(command);
      log.info("namespace {} has been deleted !", namespace);
    }
  }
}
