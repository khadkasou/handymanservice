package com.nepbrain.technology.postjob.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    PJB001("Post job not found"),
    PJB002("Post job status must be ACTIVE or INACTIVE");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
