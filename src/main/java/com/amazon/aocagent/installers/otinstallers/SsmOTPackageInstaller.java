package com.amazon.aocagent.installers.otinstallers;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.services.SSMService;
import com.amazon.aocagent.models.Context;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class SsmOTPackageInstaller implements OTInstaller {
  Context context;
  MustacheHelper mustacheHelper;
  SSMService ssmService;

  @Override
  public void init(Context context) throws Exception {
    this.context = context;

    // init ssmService
    ssmService = new SSMService(context.getRegion());
    ssmService.updateSsmAgentToLatest(context.getInstanceId());

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
    ssmService.runShellScriptCommand(context.getInstanceId(), Arrays.asList(downloadingCommand),
            context.getTestingAMI().getSSMDocument());

  }

  private void installPackage() throws Exception {
    // get installing command
    String installingCommand =
            context
                    .getTestingAMI()
                    .getInstallingCommand(context.getTestingAMI().getS3Package().getPackageName());

    // execute installing command
    ssmService.runShellScriptCommand(context.getInstanceId(), Arrays.asList(installingCommand),
            context.getTestingAMI().getSSMDocument());
  }

  private void configureAndStart() throws Exception {
    // generate configuration file
    String configContent = mustacheHelper.render(context.getOtConfig(), context);

    // write config onto the remote instance
    String configuringCommand = context.getTestingAMI().getConfiguringCommand(configContent);
    ssmService.runShellScriptCommand(context.getInstanceId(), Arrays.asList(configuringCommand),
            context.getTestingAMI().getSSMDocument());

    // start ot collector
    List<String> ssmCommands = new ArrayList<>();
    String startingCommand = context.getTestingAMI()
            .getStartingCommand(GenericConstants.EC2_WIN_CONFIG_PATH.getVal());
    ssmCommands.add(startingCommand);
    //Disable windows firewall so that the emitter can send metrics to it
    String disableFirewallCommand = context.getTestingAMI().getDisableFirewallCommand();
    if (disableFirewallCommand != null) {
      ssmCommands.add(disableFirewallCommand);
    }
    ssmService.runShellScriptCommand(context.getInstanceId(),
            ssmCommands, context.getTestingAMI().getSSMDocument());
  }
}
