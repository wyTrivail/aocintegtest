package com.amazon.aocagent.installers.emiterinstallers;

import com.amazon.aocagent.models.Context;

public interface OTEmitterInstaller {
  void init(Context context) throws Exception;

  void installAndStart() throws Exception;
}
