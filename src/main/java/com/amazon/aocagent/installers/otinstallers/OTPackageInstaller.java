package com.amazon.aocagent.installers.otinstallers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.models.Context;

import java.util.Arrays;

public class OTPackageInstaller implements OTInstaller {
  Context context;
  SSHHelper sshHelper;
  MustacheHelper mustacheHelper;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;

    // init sshHelper
    this.sshHelper =
        new SSHHelper(
            this.context.getTestingAMI().getLoginUser(),
            this.context.getInstancePublicIpAddress(),
            GenericConstants.SSH_CERT_LOCAL_PATH.getVal());

    this.mustacheHelper = new MustacheHelper();
  }

  @Override
  public void installAndStart() throws Exception {
    downloadPackage();
    installPackage();
    configureAndStart();
  }

  private void downloadPackage() throws Exception {
    // get downloading link
    String s3Key = context.getTestingAMI().getS3Package().getS3Key(context.getAgentVersion());
    String downloadingLink =
        "https://" + context.getStack().getS3BucketName() + ".s3.amazonaws.com/" + s3Key;

    // get downloading command
    String downloadingCommand =
        context
            .getTestingAMI()
            .getDownloadingCommand(
                downloadingLink,
                context.getTestingAMI().getS3Package().getPackageName());

    // execute downloading command
    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(downloadingCommand));
        });
  }

  private void installPackage() throws Exception {
    // get installing command
    String installingCommand =
        context.getTestingAMI().getInstallingCommand(
            context.getTestingAMI().getS3Package().getPackageName());

    // execute installing command
    RetryHelper.retry(
        () -> {
          sshHelper.executeCommands(Arrays.asList(installingCommand));
        });
  }

  private void configureAndStart() throws Exception {
    // generate configuration file
    String configContent = mustacheHelper.render(context.getOtConfig().getVal(), context);

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
