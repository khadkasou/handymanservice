package com.nepbrain.technology.communication.payload.response;

/*
 * @Created At 18/06/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.enums.PlatformEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecificCustomerResponse {

    private CountryResponse country;
    private MasterCategoryItemResponse gender;
    private PlatformEnum platform;
}
