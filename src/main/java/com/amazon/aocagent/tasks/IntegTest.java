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
  OTEmitterInstaller otEmitterInstaller;
  BatchedValidator batchedValidator;

  /**
   * Construct IntegTest Object.
   * @param testBed the testbed, for example: EC2
   * @param otInstaller the installer for ot package
   * @param otEmitterInstaller the installer for the emitter image
   * @param validatorList the validator list
   */
  public IntegTest(
      TestBed testBed,
      OTInstaller otInstaller,
      OTEmitterInstaller otEmitterInstaller,
      List<IValidator> validatorList) {
    this.testBed = testBed;
    this.otInstaller = otInstaller;
    this.otEmitterInstaller = otEmitterInstaller;
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

    otEmitterInstaller.init(context);
    otEmitterInstaller.installAndStart();

    batchedValidator.init(context);
    batchedValidator.validate();
  }
}
