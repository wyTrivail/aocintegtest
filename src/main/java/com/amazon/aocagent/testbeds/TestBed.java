package com.amazon.aocagent.testbeds;

import com.amazon.aocagent.models.Context;

public interface TestBed {
  void init(Context context) throws Exception;

  Context launchTestBed() throws Exception;
}
