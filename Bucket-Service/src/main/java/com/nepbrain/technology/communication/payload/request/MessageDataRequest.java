package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import lombok.Getter;
import lombok.Setter;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
@Getter
@Setter
public class MessageDataRequest extends PaginationRequest {

    private TopicEnum topic;
    private StatusEnum status;
}
