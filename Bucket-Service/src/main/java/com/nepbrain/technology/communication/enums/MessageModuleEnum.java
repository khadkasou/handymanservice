package com.nepbrain.technology.communication.enums;

import lombok.Getter;

@Getter
public enum MessageModuleEnum {
    HRM("HRM"),
    AUTH("Authentication"),
    GENERAL("General");

    private final String displayName;

    MessageModuleEnum(String displayName) {
        this.displayName = displayName;
    }
}
