package com.nepbrain.technology.communication.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TopicEnumResponse {
    private String key;
    private String value;
    private String module;
    private String moduleDisplayName;
}
