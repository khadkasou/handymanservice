package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.ChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunicationDetailRequest {
    private String subject;
    private String body;
    private ChannelEnum type;
}
