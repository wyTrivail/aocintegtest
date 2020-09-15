package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.TestCase;
import com.amazon.aocagent.models.Context;

public class IntegTestFactory {

  /**
   * run the testcase.
   *
   * @param testCase the testcase to run
   * @param context the testing context
   * @throws Exception when the test fails
   */
  public static void runTestCase(TestCase testCase, Context context) throws Exception {
    IntegTest integTest =
        new IntegTest(
            testCase.getTestBed(),
            testCase.getOtInstaller(),
            testCase.getOtEmitterInstallerList(),
            testCase.getValidatorList());

    try {
      integTest.init(context);
      integTest.execute();
    } finally {
      integTest.clean();
    }
  }
}
