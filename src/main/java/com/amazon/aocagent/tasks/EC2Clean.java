package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class EC2Clean implements ITask {
  private EC2Service ec2Service;

  @Override
  public void init(Context context) throws Exception {
    ec2Service = new EC2Service(context.getStack().getTestingRegion());
  }

  @Override
  public void execute() throws Exception {
    // fetch all the instances
    List<Instance> instanceList = ec2Service.listInstances();

    // filter instance older than 2 hours ago
    List<String> instanceIdListToBeTerminated = new ArrayList<>();

    instanceList.forEach(
        instance -> {
          // skip the instance which is not running
          if (!instance.getState().getName().equals("running")) {
            return;
          }
          log.info("check instance {}, status: {}", instance.getInstanceId(), instance.getState());
          if (!instance
              .getLaunchTime()
              .before(
                  new DateTime()
                      .minusMinutes(
                          Integer.parseInt(GenericConstants.RESOURCE_CLEAN_THRESHOLD.getVal()))
                      .toDate())) {
            return;
          }

          if (instance.getTags().size() > 1) {
            return;
          }

          if (instance.getTags().size() == 1
              && !instance
                  .getTags()
                  .get(0)
                  .equals(
                      new Tag(
                          GenericConstants.EC2_INSTANCE_TAG_KEY.getVal(),
                          GenericConstants.EC2_INSTANCE_TAG_VAL.getVal()))) {
            return;
          }

          instanceIdListToBeTerminated.add(instance.getInstanceId());
        });

    // terminate instance
    ec2Service.terminateInstance(instanceIdListToBeTerminated);
  }
}
