package com.amazon.aocagent.testamis;

public class TestAMIFactory {
  public static ITestAMI getTestAMIFromName(String amiName)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    return (ITestAMI)
        Class.forName(TestAMIFactory.class.getPackage().getName() + "." + amiName).newInstance();
  }
}
