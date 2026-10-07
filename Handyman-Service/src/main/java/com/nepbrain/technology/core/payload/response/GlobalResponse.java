package com.nepbrain.technology.core.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class GlobalResponse {
    private ApiStatusEnum status;
    @JsonIgnore
    @Builder.Default
    private HttpStatus httpStatus = HttpStatus.OK;
    private String code;
    private Object data;
    private String message;
}
