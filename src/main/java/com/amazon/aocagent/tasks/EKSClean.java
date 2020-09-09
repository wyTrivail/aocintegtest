package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.EKSTestOptionsValidationHelper;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EKSService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1PodList;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class EKSClean implements ITask {
  private Context context;
  private EKSService eksService;
  // Kubernetes client
  private ApiClient kubeClient;
  private MustacheHelper mustacheHelper;

  @Override
  public void init(Context context) throws Exception {
    EKSTestOptionsValidationHelper.checkEKSTestOptions(context);
    this.context = context;
    this.eksService = new EKSService(context.getStack().getTestingRegion());
    this.kubeClient = eksService.generateKubeClient(context);
    this.mustacheHelper = new MustacheHelper();
  }

  @Override
  public void execute() throws Exception {
    cleanSideCarDeployment();
  }

  private void cleanSideCarDeployment() throws Exception {
    String deploymentName = GenericConstants.EKS_SIDECAR_DEPLOYMENT_NAME.getVal();

    try {
      V1DeploymentList deploymentList =
          new AppsV1Api(kubeClient)
              .listNamespacedDeployment(
                  "default",
                  "true",
                  null,
                  null,
                  null,
                  "name=" + deploymentName,
                  null,
                  null,
                  null,
                  null);
      if (!deploymentList.getItems().isEmpty()) {
        log.info("Found deployment {}, deleting it", deploymentName);
        new AppsV1Api(kubeClient)
            .deleteNamespacedDeployment(
                deploymentName, "default", "true", null, 0, null, "Foreground", null);
      }
      log.info("Deployment {} doesn't exist", deploymentName);
    } catch (com.google.gson.JsonSyntaxException e) {
      // Known issue https://github.com/kubernetes-client/java/issues/86
      log.info("ignore JsonSyntaxException");
    }

    // wait for pods to be deleted
    while (true) {
      V1PodList podList =
          new CoreV1Api(kubeClient)
              .listNamespacedPod(
                  "default",
                  "true",
                  null,
                  null,
                  null,
                  "name=" + deploymentName,
                  null,
                  null,
                  null,
                  null);
      if (podList.getItems().isEmpty()) {
        break;
      }

      List<String> remainingPods = new ArrayList<>();
      podList
          .getItems()
          .forEach(
              item -> {
                remainingPods.add(item.getMetadata().getName());
              });
      log.info("Wait 10 seconds for pods {} to be deleted ", remainingPods);
      Thread.sleep(10000);
    }

    log.info("Sidecar deployment has been cleared");
  }
}
