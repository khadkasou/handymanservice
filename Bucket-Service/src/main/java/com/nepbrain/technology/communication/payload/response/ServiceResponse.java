package com.nepbrain.technology.communication.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private Boolean success = true;
    private String message;
    private Integer code;
    private Object data;

    public ServiceResponse(String message) {
        this.message = message;
    }

    public ServiceResponse(Boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public ServiceResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ServiceResponse(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}
