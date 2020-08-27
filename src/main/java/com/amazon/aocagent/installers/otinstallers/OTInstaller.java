package com.amazon.aocagent.installers.otinstallers;

import com.amazon.aocagent.models.Context;

public interface OTInstaller {

  /**
   * Init context variables.
   * @param context test context
   * @throws Exception init exception
   */
  void init(Context context) throws Exception;

  /**
   * setup integration tests resources and execute the test cases.
   * @throws Exception setup exception
   */
  void installAndStart() throws Exception;
}
