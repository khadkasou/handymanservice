package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.LanguageEnum;
import lombok.Getter;
import lombok.Setter;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
@Getter
@Setter
public class MessageDetailRequest {

    private LanguageEnum language;
    private String emailSubject;
    private String emailBody;
    private String smsBody;
    private String notificationSubject;
    private String notificationBody;
}
