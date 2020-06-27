package com.amazon.aocagent.tasks;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasicIntegTest {
    private static final Logger logger = LogManager.getLogger(BasicIntegTest.class);
    private void createInstance(){
    }

    private void installSoftware(){}

    private void configureSoftware(){}

    private void validateSoftware(){}

    public void execute(){
        logger.info("Start to perform Integration test");
        this.createInstance();
        this.installSoftware();
        this.configureSoftware();
        this.validateSoftware();
    }
}
