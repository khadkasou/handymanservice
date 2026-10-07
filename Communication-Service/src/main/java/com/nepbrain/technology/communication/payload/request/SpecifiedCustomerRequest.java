package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 18/06/2024
 * @Author suroj.awal
 */

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SpecifiedCustomerRequest {

    private List<UUID> genders;
    private List<UUID> nationalities;
}
