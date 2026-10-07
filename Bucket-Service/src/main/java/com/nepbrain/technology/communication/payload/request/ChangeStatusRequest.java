package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 12/06/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangeStatusRequest {

    private UUID id;
    private StatusEnum status;
}
