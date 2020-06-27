package com.amazon.aocagent.enums;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;

public class StackTest {

    @Test
    public void ensureNoDupConfig(){
        Set<String> bucketNameSet = new HashSet<>();
        for(Stack stack: Stack.values()){
            assertFalse("found same s3 bucket name across different stacks",bucketNameSet.contains(stack.getS3BucketName()));
            bucketNameSet.add(stack.getS3BucketName());
        }
    }

}
