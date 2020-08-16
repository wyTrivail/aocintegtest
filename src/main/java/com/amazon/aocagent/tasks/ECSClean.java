package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazon.aocagent.services.ECSService;
import com.amazonaws.services.ec2.model.Instance;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Log4j2
public class ECSClean implements ITask {
  private EC2Service ec2Service;
  private ECSService ecsService;

  @Override
  public void init(Context context) throws Exception {
    this.ec2Service = new EC2Service(context.getStack().getTestingRegion());
    this.ecsService = new ECSService(context.getStack().getTestingRegion());
  }

  @Override
  public void execute() throws Exception {

    cleanTasks();
    cleanContainerInstances();
    // static cluster name, might don't need to be cleaned for now
    //        cleanCluster();
    cleanTaskDefinitions();
  }

  private void cleanTaskDefinitions() {
    ecsService.cleanTaskDefinitions(GenericConstants.AOC_PREFIX.getVal());
  }

  private void cleanCluster() {
    ecsService.cleanCluster(GenericConstants.ECS_SIDECAR_CLUSTER.getVal());
  }

  private void cleanContainerInstances() throws Exception {
    List<Instance> instanceList =
        ec2Service.listInstancesByTag(
            GenericConstants.EC2_INSTANCE_TAG_KEY.getVal(),
            GenericConstants.EC2_INSTANCE_ECS_TAG_VAL.getVal());
    // filter instance older than 2 hours ago
    List<String> instanceIdListToBeTerminated = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR_OF_DAY, -2);
    Date twoHoursAgo = calendar.getTime();

    instanceList.forEach(
        instance -> {
          if (instance.getLaunchTime().before(twoHoursAgo) && instance.getTags().size() == 1) {
            instanceIdListToBeTerminated.add(instance.getInstanceId());
          }
        });

    log.info("terminating unused ec2 instances: {}", instanceIdListToBeTerminated);
    // terminate instance
    ec2Service.terminateInstance(instanceIdListToBeTerminated);
  }

  private void cleanTasks() throws Exception {
    ecsService.cleanTasks(GenericConstants.ECS_SIDECAR_CLUSTER.getVal());
  }
}
