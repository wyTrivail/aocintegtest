package com.amazon.aocagent.installers.emiterinstallers;

import com.amazon.aocagent.models.Context;

public interface OTEmitterInstaller {
  void init(Context context) throws Exception;

  /**
   * Install the emitter and start it.
   * @throws Exception when the emitter failed to install/start
   */
  void installAndStart() throws Exception;
}
