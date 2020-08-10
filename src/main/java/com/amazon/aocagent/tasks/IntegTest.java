package com.amazon.aocagent.tasks;

import com.amazon.aocagent.installers.emiterinstallers.OTEmitterInstaller;
import com.amazon.aocagent.installers.otinstallers.OTInstaller;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.testbeds.TestBed;
import com.amazon.aocagent.validators.BatchedValidator;
import com.amazon.aocagent.validators.IValidator;

import java.util.List;

public class IntegTest implements ITask {
  TestBed testBed;
  OTInstaller otInstaller;
  List<OTEmitterInstaller> otEmitterInstallerList;
  BatchedValidator batchedValidator;

  /**
   * Construct IntegTest Object.
   *
   * @param testBed the testbed, for example: EC2
   * @param otInstaller the installer for ot package
   * @param otEmitterInstallerList the installers for the emitter image
   * @param validatorList the validator list
   */
  public IntegTest(
      TestBed testBed,
      OTInstaller otInstaller,
      List<OTEmitterInstaller> otEmitterInstallerList,
      List<IValidator> validatorList) {
    this.testBed = testBed;
    this.otInstaller = otInstaller;
    this.otEmitterInstallerList = otEmitterInstallerList;
    this.batchedValidator = new BatchedValidator(validatorList);
  }

  @Override
  public void init(Context context) throws Exception {
    testBed.init(context);
  }

  @Override
  public void execute() throws Exception {
    Context context = testBed.launchTestBed();

    otInstaller.init(context);
    otInstaller.installAndStart();

    for (OTEmitterInstaller emitterInstaller : this.otEmitterInstallerList) {
      emitterInstaller.init(context);
      emitterInstaller.installAndStart();
    }

    batchedValidator.init(context);
    batchedValidator.validate();
  }
}
