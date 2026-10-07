package com.nepbrain.technology.communication.payload.response;

import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private UUID id;
    private TopicEnum topic;
    private StatusEnum status;
    private Boolean enableSms;
    private Boolean enableEmail;
    private Boolean enableNotification;
    private List<MessageDetailResponse> details;
}
