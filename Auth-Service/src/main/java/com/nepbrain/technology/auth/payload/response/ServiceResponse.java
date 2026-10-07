package com.nepbrain.technology.auth.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse {

    private boolean success;
    private String code;
    private String message;
    private Object data;
    private String debugMessage;

}
