package com.amazon.aocagent.tasks;

import com.amazon.aocagent.models.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EC2Test implements ITask {
    private static final Logger logger = LogManager.getLogger(EC2Test.class);

    @Override
    public void init(Context context) throws Exception {

    }

    @Override
    public void execute() {
        logger.info("start testing on ec2");
    }
}
