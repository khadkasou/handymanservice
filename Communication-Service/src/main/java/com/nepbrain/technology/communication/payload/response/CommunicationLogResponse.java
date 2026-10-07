package com.nepbrain.technology.communication.payload.response;

import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CommunicationLogResponse{
    private UUID id;
    private LocalDateTime createdAt;
    private String username;
    private String userId;
    private TopicEnum topic;
    private ChannelEnum type;
    private String subject;
    private StatusEnum status;
    private LanguageEnum language;
    private String response;
}
