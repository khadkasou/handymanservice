package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
@Getter
@Setter
public class MessageRequest {

    private UUID id;
    private TopicEnum topic;
    @NotNull
    private StatusEnum status;
    private Boolean enableSms;
    private Boolean enableEmail;
    private Boolean enableNotification;
    @Valid
    private List<MessageDetailRequest> details;
}
