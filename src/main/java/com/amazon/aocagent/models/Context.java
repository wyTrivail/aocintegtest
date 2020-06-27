package com.amazon.aocagent.models;

import com.amazon.aocagent.enums.Stack;
import lombok.Data;

@Data
public class Context {
    private Stack stack;
    private String agentVersion;
    private String localPackagesDir;
}
