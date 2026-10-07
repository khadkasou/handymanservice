package com.nepbrain.technology.apigateway.payload.request;

import java.util.Date;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogRequest {
    private Long id;
    private String requestId;
    private Date requestDate;
    private Date responseDate;
    private String url;
    private String method;
    private String requestHeader;
    private String requestBody;
    private String requestParam;
    private String response;
    private Integer statusCode;
    private String origin;
    private String device;
    private String ipAddress;
    private String userId;

}
