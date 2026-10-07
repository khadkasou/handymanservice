package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.TopicEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTPVerifyRequest {
    private String username;
    private String userId;
    private String code;
    private TopicEnum topic;
}
