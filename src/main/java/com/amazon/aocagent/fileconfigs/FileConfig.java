package com.amazon.aocagent.fileconfigs;

/**
 * Any file based config will need to implement this interface,
 * so that the mustacheHelper could render it.
 */
public interface FileConfig {
  /**
   * get the mustache file path.
   * @return file path
   */
  String getPath();
}
