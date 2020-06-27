package com.amazon.aocagent.enums;

import lombok.Getter;

@Getter
public enum GenericConstants {
    PACKAGE_NAME_PREFIX("aocagent."),
    LOCAL_PACKAGES_DIR("local-packages"),
    ;

    private String val;
    GenericConstants(String val){
        this.val = val;
    }
}
