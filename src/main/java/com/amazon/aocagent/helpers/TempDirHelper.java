package com.amazon.aocagent.helpers;

import com.amazon.aocagent.enums.GenericConstants;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Getter
public class TempDirHelper {
  private static String delimiter = "-";
  private static Path topLevelPath = Paths.get(System.getProperty("java.io.tmpdir"), "AOCTestTemp");

  /**
   * delete those temp dirs which was created 2 hours ago.
   *
   * @throws IOException fail to delete dirs
   */
  public static void cleanTempDirs() throws IOException {
    File[] subDirs = topLevelPath.toFile().listFiles();
    if (subDirs != null) {
      for (File subdir : subDirs) {
        String name = subdir.getName();
        String[] elements = name.split(delimiter);
        if (elements.length == 2) {
          String timestamp = elements[1];
          // delete dir if it was created two hours ago
          if (new Date(Long.parseLong(timestamp))
              .before(
                  new DateTime()
                      .minusMinutes(
                          Integer.parseInt(GenericConstants.RESOURCE_CLEAN_THRESHOLD.getVal()))
                      .toDate())) {
            FileUtils.deleteDirectory(subdir);
          }
        } else {
          // delete the dir with unexpected subdir name
          FileUtils.deleteDirectory(subdir);
        }
      }
    }
  }

  private Path path;

  /**
   * constructor of TempDirHelper.
   *
   * @param dirPrefix prefix of the temp dir which is going to be created
   */
  public TempDirHelper(String dirPrefix) {
    path =
        Paths.get(
            topLevelPath.toString(),
            String.format("%s%s%d", dirPrefix, delimiter, System.currentTimeMillis()));
    // create the dir
    path.toFile().mkdir();
  }

  /**
   * delete the temp dir created by this object.
   *
   * @throws IOException fail to delete file
   */
  public void deleteDir() throws IOException {
    FileUtils.deleteDirectory(path.toFile());
  }
}
