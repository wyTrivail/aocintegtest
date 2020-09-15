package com.amazon.aocagent.testbeds;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.EKSTestOptionsValidationHelper;
import com.amazon.aocagent.helpers.TempDirHelper;
import com.amazon.aocagent.models.Context;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EKSTestBed implements TestBed {
  private Context context;

  @Override
  public void init(Context context) {
    this.context = context;
  }

  @Override
  public Context launchTestBed() throws Exception {
    context.setEksTestArtifactsDir(new TempDirHelper(GenericConstants.EKS_INTEG_TEST.getVal()));
    EKSTestOptionsValidationHelper.checkEKSTestOptions(context);
    return this.context;
  }
}
