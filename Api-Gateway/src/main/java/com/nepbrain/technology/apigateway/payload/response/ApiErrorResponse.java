package com.nepbrain.technology.apigateway.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiErrorResponse {

    @Builder.Default
    private boolean success = false;
    private String code;
    private String message;
    private String debugMessage;
    private List<ErrorMessage> errors;


}
