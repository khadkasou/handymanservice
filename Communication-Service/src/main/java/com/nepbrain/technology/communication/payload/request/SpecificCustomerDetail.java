package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 14/06/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.enums.PlatformEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class SpecificCustomerDetail {

    private UUID country;
    @Enumerated(EnumType.STRING)
    private PlatformEnum platform;
    GeneralRequest gender;
}
