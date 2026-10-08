package com.nepbrain.technology.geo.service;

import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface GeoService {

    GlobalResponse countryList();

    GlobalResponse stateList(UUID countryId);

    GlobalResponse cityList(UUID stateId);
}
