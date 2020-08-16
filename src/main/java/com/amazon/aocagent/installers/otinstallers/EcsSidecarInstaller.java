package com.amazon.aocagent.installers.otinstallers;

import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.ECSService;

public class EcsSidecarInstaller implements OTInstaller {
  private Context context;
  private ECSService ecsService;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;
    this.ecsService = new ECSService(context.getStack().getTestingRegion());
  }

  @Override
  public void installAndStart() throws Exception {
    // create and run ECS sidecar task definitions
    ecsService.createAndRunTaskDefinition(this.context);
  }
}
