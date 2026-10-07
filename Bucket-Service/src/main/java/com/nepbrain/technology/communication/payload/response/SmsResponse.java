package com.nepbrain.technology.communication.payload.response;

/*
 * @Created At 17/03/2024
 * @Author suroj.awal
 */

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsResponse {
    private Integer responseCode;
    private String responseDescription;
    private String smsText;
}
