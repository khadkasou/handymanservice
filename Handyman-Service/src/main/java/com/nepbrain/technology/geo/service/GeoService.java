package com.nepbrain.technology.geo.service;
import com.nepbrain.technology.geo.payload.response.*;
import java.util.List; import java.util.UUID;
public interface GeoService {
    List<CountryResponse> countryList();
    List<StateResponse> stateList(UUID countryId);
    List<CityResponse> cityList(UUID stateId);
}
