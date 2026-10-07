package com.nepbrain.technology.communication.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/*
 * @Created At 21/05/2024
 * @Author suroj.awal
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MfaResponse {

    @JsonProperty(value = "QRImage")
    private String qrImage;
    private String code;
}
