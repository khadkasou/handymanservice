package com.nepbrain.technology.communication.payload.response;

import com.nepbrain.technology.communication.enums.LanguageEnum;
import lombok.*;

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
public class MessageDetailResponse {

    private UUID id;
    private LanguageEnum language;
    private String emailSubject;
    private String emailBody;
    private String smsBody;
    private String notificationSubject;
    private String notificationBody;
}
