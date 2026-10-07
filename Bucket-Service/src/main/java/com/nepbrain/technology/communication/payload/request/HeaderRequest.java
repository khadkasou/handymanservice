package com.nepbrain.technology.communication.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeaderRequest {
    private String id;
    private String userId;
    private String deviceId;
    private String deviceType;
    private String deviceVersion;
    private String channelId;
    private String acceptLanguage;
}
