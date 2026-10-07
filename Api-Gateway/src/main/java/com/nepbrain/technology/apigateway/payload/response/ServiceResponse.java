package com.nepbrain.technology.apigateway.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceResponse {

    private boolean success;
    private String code;
    private String message;
    private Object data;


}
