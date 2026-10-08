package com.nepbrain.technology.auth.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailVerifyRequest {

    private String email;
    private String verificationId;
    private String otpCode;
}
