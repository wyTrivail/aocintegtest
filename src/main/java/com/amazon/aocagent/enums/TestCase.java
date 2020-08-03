package com.amazon.aocagent.enums;

import com.amazon.aocagent.installers.emiterinstallers.OTEmitterInstaller;
import com.amazon.aocagent.installers.emiterinstallers.OTMetricAndTraceEmitterInstaller;
import com.amazon.aocagent.installers.otinstallers.OTInstaller;
import com.amazon.aocagent.installers.otinstallers.OTPackageInstaller;
import com.amazon.aocagent.testbeds.EC2TestBed;
import com.amazon.aocagent.testbeds.TestBed;
import com.amazon.aocagent.validators.IValidator;
import com.amazon.aocagent.validators.MetricValidator;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum TestCase {
  EC2Test(
      new EC2TestBed(),
      new OTPackageInstaller(),
      new OTMetricAndTraceEmitterInstaller(),
      Arrays.asList(new MetricValidator())),
  ;

  private TestBed testBed;
  private OTInstaller otInstaller;
  private OTEmitterInstaller otEmitterInstaller;
  private List<IValidator> validatorList;

  TestCase(
      TestBed testBed,
      OTInstaller otInstaller,
      OTEmitterInstaller otEmitterInstaller,
      List<IValidator> validatorList) {
    this.testBed = testBed;
    this.otInstaller = otInstaller;
    this.otEmitterInstaller = otEmitterInstaller;
    this.validatorList = validatorList;
  }
}
