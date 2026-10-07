package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.PlatformEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunicationRequest {
    private String username;
    private Long mobile;
    private PlatformEnum platform;
    private String email;
    private String countryCode;
    private LanguageEnum language;
    private TopicEnum topic;
    private List<String> tokens;
    private Map<String, String> data;
    private ChannelEnum sendTo;
}
