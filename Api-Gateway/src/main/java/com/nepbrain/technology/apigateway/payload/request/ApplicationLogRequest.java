package com.nepbrain.technology.apigateway.payload.request;

import com.nepbrain.technology.apigateway.enums.ApplicationStatusEnum;
import java.util.Date;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationLogRequest {
    private Long id;
    private String service;
    private Date logDate;
    private ApplicationStatusEnum status;

}
