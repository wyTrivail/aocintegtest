package com.amazon.aocagent.tasks;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.EC2Service;
import com.amazonaws.services.ec2.model.Instance;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EC2Clean implements ITask {
  private EC2Service ec2Service;

  @Override
  public void init(Context context) throws Exception {
    ec2Service = new EC2Service(context.getRegion());
  }

  @Override
  public void execute() throws Exception {
    // get ec2 instance with integ-test tag
    List<Instance> instanceList =
        ec2Service.listInstancesByTag(
            GenericConstants.EC2_INSTANCE_TAG_KEY.getVal(),
            GenericConstants.EC2_INSTANCE_TAG_VAL.getVal());

    // filter instance older than 2 hours ago
    List<String> instanceIdListToBeTerminated = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR_OF_DAY, -2);
    Date twoHoursAgo = calendar.getTime();

    instanceList.forEach(
        instance -> {
          if (instance.getLaunchTime().before(twoHoursAgo)) {
            instanceIdListToBeTerminated.add(instance.getInstanceId());
          }
        });

    // terminate instance
    ec2Service.terminateInstance(instanceIdListToBeTerminated);
  }

  @Override
  public String response() throws Exception {
    return null;
  }
}
