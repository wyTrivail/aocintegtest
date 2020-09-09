package com.amazon.aocagent.helpers;

import com.amazon.aocagent.models.Context;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Log4j2
public class EKSTestOptionsValidationHelper {
  /**
   * validate EKS test options.
   *
   * @param context test context
   */
  public static void checkEKSTestOptions(Context context) {
    if (context.getEksClusterName() == null) {
      throw new RuntimeException("EKS test without specifying cluster name");
    }

    if (context.getIamAuthenticatorPath() == null) {
      // set default aws-iam-authenticator path
      String binaryUrl = null;
      String binaryName = null;

      switch (detectOS()) {
        case "MacOS":
          binaryUrl =
              "https://amazon-eks.s3.us-west-2.amazonaws.com/1.17.9/2020-08-04/bin/darwin/amd64/aws-iam-authenticator";
          binaryName = "aws-iam-authenticator";
          break;
        case "Linux":
          binaryUrl =
              "https://amazon-eks.s3.us-west-2.amazonaws.com/1.17.9/2020-08-04/bin/linux/amd64/aws-iam-authenticator";
          binaryName = "aws-iam-authenticator";
          break;
        case "Windows":
          binaryUrl =
              "https://amazon-eks.s3.us-west-2.amazonaws.com/1.17.9/2020-08-04/bin/windows/amd64/aws-iam-authenticator.exe";
          binaryName = "aws-iam-authenticator.exe";
          break;
        default:
          break;
      }

      if (binaryUrl != null) {
        File binaryFile =
            new File(
                EKSTestOptionsValidationHelper.class.getResource("/").getPath()
                    + "/"
                    + binaryName);
        try {
          FileUtils.copyURLToFile(new URL(binaryUrl), binaryFile);
        } catch (IOException e) {
          throw new RuntimeException("Download aws-iam-authenticator " + binaryUrl + " failed.");
        }
        binaryFile.setExecutable(true);
        context.setIamAuthenticatorPath(binaryFile.getPath());
      } else {
        throw new RuntimeException("EKS test without iam authenticator");
      }
    }
  }

  private static String detectOS() {
    if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
      return "MacOS";
    }
    if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
      return "Windows";
    }
    if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
      return "Linux";
    }
    return "Unknown";
  }
}
