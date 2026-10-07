package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 27/11/2024
 * @Author Suroj Awal
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkNotificationWithDiffMessageRequest {

    List<NotificationMessageRequest> messageRequests;

}
