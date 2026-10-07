package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSentRequest {
    private TopicEnum topic;
    private String username;
    private String countryCode;
    private LanguageEnum language;
    private ChannelEnum type;
    private Map<String, String> data;
}
