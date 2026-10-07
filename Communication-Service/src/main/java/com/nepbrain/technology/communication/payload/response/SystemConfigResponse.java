package com.nepbrain.technology.communication.payload.response;

/*
 * @Created At 29/04/2024
 * @Author suroj.awal
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigResponse {

    UUID id;
    String companyLegalName;
    String companyBrandName;
    String description;
    String image;
    String dateFormat;
    String region;
    CountryResponse country;
    String currency;
}
