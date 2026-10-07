package com.nepbrain.technology.core.payload.request;

import com.nepbrain.technology.core.enums.ApplicationStatusEnum;
import lombok.*;

import java.util.Date;

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
