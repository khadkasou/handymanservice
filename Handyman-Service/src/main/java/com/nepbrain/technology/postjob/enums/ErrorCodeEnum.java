package com.nepbrain.technology.postjob.enums;
import lombok.Getter;
@Getter
public enum ErrorCodeEnum {
    PJB001("Post job not found");
    private final String code;
    ErrorCodeEnum(String code) { this.code = code; }
}
