package com.amazon.aocagent.installers.otinstallers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.enums.OSType;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.helpers.SSHHelper;
import com.amazon.aocagent.helpers.SSMHelper;
import com.amazon.aocagent.models.Context;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

import static com.amazon.aocagent.helpers.SSMHelper.RUN_POWER_SHELL_SCRIPT_DOCUMENT;

@Log4j2
public class OTPackageInstaller implements OTInstaller {
  Context context;
  SSHHelper sshHelper;
  MustacheHelper mustacheHelper;
  SSMHelper ssmHelper;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;

    if (context.getTestingAMI().getOSType().equals(OSType.WINDOWS)){
        // init ssmHelper
        ssmHelper = new SSMHelper(context.getRegion());
        ssmHelper.updateSsmAgentToLatest(context.getInstanceId());
    } else {
        // init sshHelper
        this.sshHelper =
            new SSHHelper(
                this.context.getTestingAMI().getLoginUser(),
                this.context.getInstancePublicIpAddress(),
                GenericConstants.SSH_CERT_LOCAL_PATH.getVal());
    }

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
                downloadingLink, context.getTestingAMI().getS3Package().getPackageName());

      // execute downloading command
    if (context.getTestingAMI().getOSType().equals(OSType.WINDOWS)) {
        ssmHelper.runShellScriptCommand(context.getInstanceId(), Arrays.asList(downloadingCommand), RUN_POWER_SHELL_SCRIPT_DOCUMENT);
    } else {
        RetryHelper.retry(
            () -> {
                sshHelper.executeCommands(Arrays.asList(downloadingCommand));
            });
    }
  }

  private void installPackage() throws Exception {
    // get installing command
    String installingCommand =
        context
            .getTestingAMI()
            .getInstallingCommand(context.getTestingAMI().getS3Package().getPackageName());

      // execute installing command
    if (context.getTestingAMI().getOSType().equals(OSType.WINDOWS)) {
        ssmHelper.runShellScriptCommand(context.getInstanceId(), Arrays.asList(installingCommand), RUN_POWER_SHELL_SCRIPT_DOCUMENT);
    } else {
        RetryHelper.retry(
            () -> {
                sshHelper.executeCommands(Arrays.asList(installingCommand));
            });
    }
  }

  private void configureAndStart() throws Exception {
    // generate configuration file
    String configContent = mustacheHelper.render(context.getOtConfig(), context);

    // write config onto the remote instance
    if (context.getTestingAMI().getOSType().equals(OSType.WINDOWS)) {
        String configuringCommand =
            String.format(
                "Set-Content -Path %s -Value \"%s\"\n",
                GenericConstants.EC2_WIN_CONFIG_PATH.getVal(), configContent);
        ssmHelper.runShellScriptCommand(context.getInstanceId(), Arrays.asList(configuringCommand), RUN_POWER_SHELL_SCRIPT_DOCUMENT);
    } else {
        String configuringCommand =
            String.format(
                "(\n" + "cat<<EOF\n" + "%s\n" + "EOF\n" + ") | sudo tee %s",
                configContent, GenericConstants.EC2_CONFIG_PATH.getVal());

        RetryHelper.retry(
            () -> {
                sshHelper.executeCommands(Arrays.asList(configuringCommand));
            });
    }
      // start ot collector
      if (context.getTestingAMI().getOSType().equals(OSType.WINDOWS)) {
          String startingCommand = context.getTestingAMI().getStartingCommand(GenericConstants.EC2_WIN_CONFIG_PATH.getVal());
          //Disable windows firewall so that the emitter can send metrics to it
          String disableFirewallCommand = "Set-NetFirewallProfile -Profile Public -Enabled False";
          ssmHelper.runShellScriptCommand(context.getInstanceId(), Arrays.asList(startingCommand, disableFirewallCommand), RUN_POWER_SHELL_SCRIPT_DOCUMENT);
      } else {
          String startingCommand = context.getTestingAMI().getStartingCommand(GenericConstants.EC2_CONFIG_PATH.getVal());
          RetryHelper.retry(
              () -> {
                  sshHelper.executeCommands(Arrays.asList(startingCommand));
              });
      }
  }
}
