package com.amazon.aocagent.installers.otinstallers;

import static com.amazon.aocagent.helpers.EKSTestOptionsValidationHelper.manifestsDir;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.fileconfigs.EksSidecarManifestTemplate;
import com.amazon.aocagent.helpers.CommandExecutionHelper;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EKSService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Log4j2
public class EKSInstaller implements OTInstaller {
  private Context context;
  private EKSService eksService;
  private MustacheHelper mustacheHelper;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;
    this.eksService = new EKSService(context.getStack().getTestingRegion());
    this.mustacheHelper = new MustacheHelper();
  }

  @Override
  public void installAndStart() throws Exception {
    setupEKSContext(context);
    generateEKSTestManifestFile(context);
    deployEKSTestManifestFile(context);

    log.info("EKS integ test {} has been deployed", context.getEksTestManifestName());
  }

  private void setupEKSContext(Context context) {
    context.setAocImage(context.getStack().getTestingImageRepoName()
            + ":" + context.getAgentVersion());
    context.setDataEmitterImage(GenericConstants.TRACE_EMITTER_DOCKER_IMAGE_URL.getVal());
    // Uses current timestamp as instance id which is used as a uniq test id
    context.setInstanceId(String.valueOf(System.currentTimeMillis()));
    if (context.getEksTestManifestName() == null) {
      context.setEksTestManifestName(GenericConstants.EKS_DEFAULT_TEST_MANIFEST.getVal());
    }
  }

  private void generateEKSTestManifestFile(Context context) throws Exception {
    String manifestYamlContent =
        mustacheHelper.render(
            new EksSidecarManifestTemplate(
                String.format("/templates/eks/%s.mustache", context.getEksTestManifestName())),
            context);

    log.info("EKS sidecar integ test deployment yaml content:\n" + manifestYamlContent);

    FileUtils.writeStringToFile(
        new File(String.format("%s/%s.yml", manifestsDir, context.getEksTestManifestName())),
        manifestYamlContent);
  }

  private void deployEKSTestManifestFile(Context context) throws Exception {
    String command =
        String.format(
            "%s apply -f %s --kubeconfig %s",
            context.getKubectlPath(),
            String.format("%s/%s.yml", manifestsDir, context.getEksTestManifestName()),
            context.getKubeconfigPath());
    CommandExecutionHelper.runChildProcessWithAWSCred(command);
  }
}
