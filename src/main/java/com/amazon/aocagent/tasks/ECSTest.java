package com.amazon.aocagent.tasks;

import com.amazon.aocagent.models.Context;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ECSTest implements ITask {
    @Override
    public void init(Context context) throws Exception {

    }

    @Override
    public void execute() {
        log.info("Start ECS Test");
    }
}
