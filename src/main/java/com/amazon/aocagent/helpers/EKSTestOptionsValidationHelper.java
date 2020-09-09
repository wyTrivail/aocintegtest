package com.amazon.aocagent.helpers;

import com.amazon.aocagent.models.Context;
import lombok.extern.log4j.Log4j2;

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
      if (System.getProperty("os.name").startsWith("Mac")) {
        log.info("default aws-iam-authenticator for Mac OS is used");
        context.setIamAuthenticatorPath(
            EKSTestOptionsValidationHelper.class
                .getResource("/tools/MacOS/aws-iam-authenticator")
                .getPath());
      } else {
        throw new RuntimeException("EKS test without specifying authenticator path");
      }
    }
  }
}
