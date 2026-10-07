package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 24/06/2024
 * @Author suroj.awal
 */

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CountryListRequest {

    private List<UUID> countryIds;
}
