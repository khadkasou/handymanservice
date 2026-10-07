package com.nepbrain.technology.communication.payload.response;

/*
 * @Created At 14/06/2024
 * @Author suroj.awal
 */

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnumResponse {

    private String key;
    private String value;
}
