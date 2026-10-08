package com.nepbrain.technology.geo.service.impl;

import com.nepbrain.technology.geo.entity.CityEntity;
import com.nepbrain.technology.geo.entity.CountryEntity;
import com.nepbrain.technology.geo.entity.StateEntity;
import com.nepbrain.technology.geo.payload.response.CityResponse;
import com.nepbrain.technology.geo.payload.response.CountryResponse;
import com.nepbrain.technology.geo.payload.response.StateResponse;
import com.nepbrain.technology.geo.repository.CityRepository;
import com.nepbrain.technology.geo.repository.CountryRepository;
import com.nepbrain.technology.geo.repository.StateRepository;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.geo.service.GeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeoServiceImpl implements GeoService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    @Override
    public GlobalResponse countryList() {
        List<CountryResponse> countries = new ArrayList<>();
        for (CountryEntity country : countryRepository.findAll()) {
            countries.add(CountryResponse.builder()
                    .id(country.getId())
                    .name(country.getName())
                    .code(country.getCode())
                    .build());
        }
        return GlobalResponseBuilder.buildSuccessResponse(countries);
    }

    @Override
    public GlobalResponse stateList(UUID countryId) {
        List<StateResponse> states = new ArrayList<>();
        for (StateEntity state : stateRepository.findByCountryId(countryId)) {
            states.add(StateResponse.builder()
                    .id(state.getId())
                    .name(state.getName())
                    .countryId(state.getCountryId())
                    .build());
        }
        return GlobalResponseBuilder.buildSuccessResponse(states);
    }

    @Override
    public GlobalResponse cityList(UUID stateId) {
        List<CityResponse> cities = new ArrayList<>();
        for (CityEntity city : cityRepository.findByStateId(stateId)) {
            cities.add(CityResponse.builder()
                    .id(city.getId())
                    .name(city.getName())
                    .stateId(city.getStateId())
                    .build());
        }
        return GlobalResponseBuilder.buildSuccessResponse(cities);
    }
}
