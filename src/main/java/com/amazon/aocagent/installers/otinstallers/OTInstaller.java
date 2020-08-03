package com.amazon.aocagent.installers.otinstallers;

import com.amazon.aocagent.models.Context;

public interface OTInstaller {
  void init(Context context) throws Exception;

  void installAndStart() throws Exception;
}
