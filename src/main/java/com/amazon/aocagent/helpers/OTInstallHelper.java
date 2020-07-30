package com.amazon.aocagent.helpers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.mustache.TemplateProvider;
import com.amazon.aocagent.mustache.models.EC2ConfigTemplate;

import java.util.Arrays;

public class OTInstallHelper {

  private SSHHelper sshHelper;
  private Context context;
  private TemplateProvider templateProvider;

  /**
   * Construct OTInstallHelper.
   *
   * @param sshHelper the sshHelper to execute command remotely
   * @param context the running context, mainly to get the ami
   */
  public OTInstallHelper(SSHHelper sshHelper, Context context) {
    this.sshHelper = sshHelper;
    this.context = context;
    this.templateProvider = new TemplateProvider();
  }

  /**
   * Install, Configure and Start OT collector.
   *
   * @throws Exception when remote commands fail to execute
   */
  public void installAndStart() throws Exception {
    downloadPackage();
    installPackage();
    configureAndStart();
  }

  private void downloadPackage() throws Exception {
    // get downloading link
    String s3Key = context.getTestingAMI().getS3Key(context.getAgentVersion());
    String downloadingLink =
        "https://" + context.getStack().getS3BucketName() + ".s3.amazonaws.com/" + s3Key;

    // get downloading command
    String downloadingCommand =
        context
            .getTestingAMI()
            .getDownloadingCommand(downloadingLink, context.getTestingAMI().getPackageName());

    // execute downloading command
    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(downloadingCommand));
        });
  }

  private void installPackage() throws Exception {
    // get installing command
    String installingCommand =
        context.getTestingAMI().getInstallingCommand(context.getTestingAMI().getPackageName());

    // execute installing command
    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(installingCommand));
        });
  }

  private void configureAndStart() throws Exception {
    // generate configuration file
    String configContent = templateProvider.renderTemplate(new EC2ConfigTemplate());

    // write config onto the remote instance
    String configuringCommand =
        String.format(
            "(\n" + "cat<<EOF\n" + "%s\n" + "EOF\n" + ") | sudo tee %s",
            configContent, GenericConstants.EC2_CONFIG_PATH.getVal());

    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(configuringCommand));
        });

    // start ot collector
    String startingCommand =
        context.getTestingAMI().getStartingCommand(GenericConstants.EC2_CONFIG_PATH.getVal());
    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(startingCommand));
        });
  }
}
