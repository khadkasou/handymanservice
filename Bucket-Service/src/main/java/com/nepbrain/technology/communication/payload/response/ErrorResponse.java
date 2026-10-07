package com.nepbrain.technology.communication.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Boolean success = false;
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
