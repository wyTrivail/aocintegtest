package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR_OF_DAY, -2);
    Date twoHoursAgo = calendar.getTime();

    instanceList.forEach(
        instance -> {
          if (instance.getTags().size() != 0
              && !instance
                  .getTags()
                  .contains(
                      new Tag(
                          GenericConstants.EC2_INSTANCE_TAG_KEY.getVal(),
                          GenericConstants.EC2_INSTANCE_TAG_VAL.getVal()))) {
            // only clean the integ-test instances and the instances without tags
            return;
          }
          if (instance.getLaunchTime().before(twoHoursAgo) && instance.getTags().size() == 1) {
            instanceIdListToBeTerminated.add(instance.getInstanceId());
          }
        });

    // terminate instance
    ec2Service.terminateInstance(instanceIdListToBeTerminated);
  }
}
