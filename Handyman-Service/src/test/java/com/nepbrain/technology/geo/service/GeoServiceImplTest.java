package com.nepbrain.technology.geo.service;

import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.geo.entity.CityEntity;
import com.nepbrain.technology.geo.entity.CountryEntity;
import com.nepbrain.technology.geo.entity.StateEntity;
import com.nepbrain.technology.geo.payload.response.CityResponse;
import com.nepbrain.technology.geo.payload.response.CountryResponse;
import com.nepbrain.technology.geo.payload.response.StateResponse;
import com.nepbrain.technology.geo.repository.CityRepository;
import com.nepbrain.technology.geo.repository.CountryRepository;
import com.nepbrain.technology.geo.repository.StateRepository;
import com.nepbrain.technology.geo.service.impl.GeoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GeoServiceImplTest {

    @Mock private CountryRepository countryRepository;
    @Mock private StateRepository stateRepository;
    @Mock private CityRepository cityRepository;

    @Spy private ModelMapper modelMapper = configuredMapper();

    @InjectMocks private GeoServiceImpl geoService;

    private static ModelMapper configuredMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        return mapper;
    }

    @Test
    void countryList_success() {
        CountryEntity country = CountryEntity.builder().name("Nepal").code("NP").status(StatusEnum.ACTIVE).build();
        country.setId(UUID.randomUUID());
        when(countryRepository.findAll()).thenReturn(List.of(country));

        List<CountryResponse> result = (List<CountryResponse>) geoService.countryList().getData();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Nepal");
        assertThat(result.getFirst().getCode()).isEqualTo("NP");
        verify(countryRepository).findAll();
    }

    @Test
    void stateList_success() {
        UUID countryId = UUID.randomUUID();
        StateEntity state = StateEntity.builder()
                .name("Bagmati")
                .countryId(countryId)
                .status(StatusEnum.ACTIVE)
                .build();
        state.setId(UUID.randomUUID());
        when(stateRepository.findByCountryId(countryId)).thenReturn(List.of(state));

        List<StateResponse> result = (List<StateResponse>) geoService.stateList(countryId).getData();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Bagmati");
        assertThat(result.getFirst().getCountryId()).isEqualTo(countryId);
        verify(stateRepository).findByCountryId(countryId);
    }

    @Test
    void cityList_success() {
        UUID stateId = UUID.randomUUID();
        CityEntity city = CityEntity.builder()
                .name("Kathmandu")
                .stateId(stateId)
                .status(StatusEnum.ACTIVE)
                .build();
        city.setId(UUID.randomUUID());
        when(cityRepository.findByStateId(stateId)).thenReturn(List.of(city));

        List<CityResponse> result = (List<CityResponse>) geoService.cityList(stateId).getData();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Kathmandu");
        assertThat(result.getFirst().getStateId()).isEqualTo(stateId);
        verify(cityRepository).findByStateId(stateId);
    }
}
