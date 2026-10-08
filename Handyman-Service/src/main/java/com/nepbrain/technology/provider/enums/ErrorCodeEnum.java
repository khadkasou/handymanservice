package com.nepbrain.technology.provider.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    PRV001("Provider not found"),
    PRV002("Favourite provider not found"),
    PRV003("Rating not found"),
    PRV004("Favourite provider status must be ACTIVE or INACTIVE"),
    PRV005("Rating status must be ACTIVE or INACTIVE");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
