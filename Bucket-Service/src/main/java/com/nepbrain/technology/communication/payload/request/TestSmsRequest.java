package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.SmsProviderEnum;
import jakarta.validation.constraints.NotBlank;
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
public class TestSmsRequest {

    @NotBlank
    private String toPhone;

    private SmsProviderEnum smsProvider;
    private String smsApiUrl;
    private String smsApiKey;
    private String smsSenderId;
    private String smsAuthFieldName;
}
