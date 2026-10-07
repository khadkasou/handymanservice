package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.SmsProviderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMessagingConfigRequest {

    private Boolean enableEmail;
    private Boolean enableSms;
    private Boolean enableNotification;

    private String fromName;
    private String fromEmail;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private Boolean smtpUseTls;

    /** Active SMS provider. */
    private SmsProviderEnum smsProvider;

    private String sparrowSmsToken;
    private String sparrowSmsFrom;
    private String sparrowSmsApiUrl;
    private String sparrowSmsAuthFieldName;

    private String aakashSmsToken;
    private String aakashSmsFrom;
    private String aakashSmsApiUrl;
    private String aakashSmsAuthFieldName;
}
