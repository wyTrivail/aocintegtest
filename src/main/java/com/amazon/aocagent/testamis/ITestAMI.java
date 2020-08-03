package com.amazon.aocagent.testamis;

import java.util.List;

public interface ITestAMI {
  String getAMIId();

  String getLoginUser();

  String getS3Key(String version);

  String getDownloadingCommand(String fromUrl, String toLocation);

  String getInstallingCommand(String packagePath);

  String getStartingCommand(String configPath);

  List<String> getDockerInstallingCommands();

  String getPackageName();
}
