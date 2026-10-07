package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 10/04/2024
 * @Author suroj.awal
 */

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class NotificationRequest {

    private List<String> tokens;
    private NotificationMessageRequest notification;
}
