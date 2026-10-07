package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.TopicEnum;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTPGenerateRequest {
    private String userId;
    private String username;
    private Long mobile;
    private String email;
    private String countryCode;
    private TopicEnum topicEnum;
}
