package com.nepbrain.technology.communication.payload.request;

import lombok.*;

/*
 * @Created At 12/03/2024
 * @Author suroj.awal
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailRequest {
    private String to;
    private String subject;
    private String body;
}
