package com.amazon.aocagent.enums;

import com.amazon.aocagent.installers.emiterinstallers.OTEmitterInstaller;
import com.amazon.aocagent.installers.emiterinstallers.OTMetricAndTraceEmitterInstaller;
import com.amazon.aocagent.installers.otinstallers.EcsSidecarInstaller;
import com.amazon.aocagent.installers.otinstallers.OTInstaller;
import com.amazon.aocagent.installers.otinstallers.OTPackageInstaller;
import com.amazon.aocagent.testbeds.EC2TestBed;
import com.amazon.aocagent.testbeds.ECSTestBed;
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
      Arrays.asList(new OTMetricAndTraceEmitterInstaller()),
      Arrays.asList(new MetricValidator())),

  // run AOC with data emitter in ECS as sidecar
  // tested both ECS fargate and EC2 modes
  ECS_SIDECAR(
          new ECSTestBed(),
          new EcsSidecarInstaller(),
          Arrays.asList(), // data emitter is included in sidecar installer
          Arrays.asList(new MetricValidator())),
  ;

  private TestBed testBed;
  private OTInstaller otInstaller;
  private List<OTEmitterInstaller> otEmitterInstallerList;
  private List<IValidator> validatorList;

  TestCase(
      TestBed testBed,
      OTInstaller otInstaller,
      List<OTEmitterInstaller> otEmitterInstallerList,
      List<IValidator> validatorList) {
    this.testBed = testBed;
    this.otInstaller = otInstaller;
    this.otEmitterInstallerList = otEmitterInstallerList;
    this.validatorList = validatorList;
  }
}
