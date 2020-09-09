package com.amazon.aocagent.installers.otinstallers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.fileconfigs.EksSidecarManifestTemplate;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EKSService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EKSInstaller implements OTInstaller {
  private Context context;
  private EKSService eksService;
  // Kubernetes client
  private ApiClient kubeClient;
  private MustacheHelper mustacheHelper;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;
    this.eksService = new EKSService(context.getStack().getTestingRegion());
    this.kubeClient = eksService.generateKubeClient(context);
    this.mustacheHelper = new MustacheHelper();
  }

  @Override
  public void installAndStart() throws Exception {
    setupEKSContext(context);
    String deploymentYml =
        mustacheHelper.render(EksSidecarManifestTemplate.MANIFEST_TEMPLATE, context);

    log.info("EKS sidecar integ test deployment yaml content:\n" + deploymentYml);

    V1Deployment deployment = Yaml.loadAs(deploymentYml, V1Deployment.class);

    new AppsV1Api(kubeClient).createNamespacedDeployment("default", deployment, "true", null, "");
    log.info(
        "EKS sidecar integ test deployment {} has been created", context.getEksDeploymentName());
  }

  private void setupEKSContext(Context context) {
    context.setAocImage(GenericConstants.AOC_IMAGE.getVal() + context.getAgentVersion());
    context.setDataEmitterImage(GenericConstants.TRACE_EMITTER_DOCKER_IMAGE_URL.getVal());
    context.setRegion(context.getStack().getTestingRegion());
    context.setEksDeploymentName(GenericConstants.EKS_SIDECAR_DEPLOYMENT_NAME.getVal());
    // Uses current timestamp as instance id which is used as a uniq test id
    context.setInstanceId(String.valueOf(System.currentTimeMillis()));
  }
}
