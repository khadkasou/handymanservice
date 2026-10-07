package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.ApplicationStatusEnum;
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
