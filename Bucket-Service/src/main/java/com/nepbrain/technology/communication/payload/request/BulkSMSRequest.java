package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 18/06/2024
 * @Author suroj.awal
 */

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BulkSMSRequest {

    private Long mobile;
    private String message;
}
