package com.nepbrain.technology.communication.enums;

/*
 * @Created At 14/06/2024
 * @Author suroj.awal
 */

import lombok.Getter;

@Getter
public enum GenderEnum {

    MALE("Male"),
    FEMALE("Female");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }
}
