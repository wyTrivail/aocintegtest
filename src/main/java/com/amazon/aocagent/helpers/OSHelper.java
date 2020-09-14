package com.amazon.aocagent.helpers;

public class OSHelper {
  private static String OS = System.getProperty("os.name").toLowerCase();

  /**
   * check whether current OS is Windows.
   *
   * @return current OS is Windows or not
   */
  public static boolean isWindows() {
    return OS.startsWith("win");
  }

  /**
   * check whether current OS is MacOS X.
   *
   * @return current OS is MacOS X or not
   */
  public static boolean isMac() {
    return OS.startsWith("mac");
  }

  /**
   * check whether current OS is Linux.
   *
   * @return current OS is Linux or not
   */
  public static boolean isLinux() {
    return OS.startsWith("linux");
  }
}
