package com.nepbrain.technology.communication.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/*
 * Request DTO for Firebase/push notifications (title, body, image, fcm token, data).
 * Use MessageRequest for Message entity CRUD.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageRequest {

    private String title;
    private String body;
    private String image;
    private String fcmId;
    private Map<String, String> data;
}
