package com.nepbrain.technology.core.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class GlobalResponse {
    private ApiStatusEnum status;
    @JsonIgnore
    @Builder.Default
    private HttpStatus httpStatus = HttpStatus.OK;
    private String code;
    private Object data;
    private String message;
}
