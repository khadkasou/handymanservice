package com.nepbrain.technology.geo.service.impl;
import com.nepbrain.technology.geo.payload.response.*;
import com.nepbrain.technology.geo.repository.*;
import com.nepbrain.technology.geo.service.GeoService;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class GeoServiceImpl implements GeoService {
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;
    @Override public List<CountryResponse> countryList() {
        return countryRepository.findAll().stream().map(c -> modelMapper.map(c, CountryResponse.class)).collect(Collectors.toList());
    }
    @Override public List<StateResponse> stateList(UUID countryId) {
        return stateRepository.findByCountryId(countryId).stream().map(s -> modelMapper.map(s, StateResponse.class)).collect(Collectors.toList());
    }
    @Override public List<CityResponse> cityList(UUID stateId) {
        return cityRepository.findByStateId(stateId).stream().map(c -> modelMapper.map(c, CityResponse.class)).collect(Collectors.toList());
    }
}
