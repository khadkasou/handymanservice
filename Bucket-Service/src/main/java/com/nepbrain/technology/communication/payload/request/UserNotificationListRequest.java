package com.nepbrain.technology.communication.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotificationListRequest extends PaginationRequest {
    private Boolean read;
    /** Optional title/body/topic/requestNumber filter */
    private String search;
}
