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
public class TestEmailRequest {

    @NotBlank
    private String toEmail;

    @NotBlank
    private String subject;

    @NotBlank
    private String bodyHtml;

    private String fromName;
    private String fromEmail;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private Boolean smtpUseTls;
}
