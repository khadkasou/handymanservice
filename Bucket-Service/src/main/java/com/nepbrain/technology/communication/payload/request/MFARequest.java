package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 21/05/2024
 * @Author suroj.awal
 */

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MFARequest {

    private String secret;
}
