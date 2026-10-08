package com.nepbrain.technology.geo.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.geo.payload.request.GeoListRequest;
import com.nepbrain.technology.geo.payload.response.CityResponse;
import com.nepbrain.technology.geo.payload.response.CountryResponse;
import com.nepbrain.technology.geo.payload.response.StateResponse;
import com.nepbrain.technology.geo.service.GeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GeoControllerTest {

    @Mock private GeoService geoService;

    @InjectMocks private GeoController geoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(geoController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void countryList_returnsSuccess() throws Exception {
        when(geoService.countryList()).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(CountryResponse.builder()
                .id(UUID.randomUUID())
                .name("Nepal")
                .code("NP")
                .build())).build());

        mockMvc.perform(post("/api/v2/geo/country-list")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data[0].name").value("Nepal"));

        verify(geoService).countryList();
    }

    @Test
    void stateList_returnsSuccess() throws Exception {
        UUID countryId = UUID.randomUUID();
        GeoListRequest request = GeoListRequest.builder().countryId(countryId).build();
        when(geoService.stateList(countryId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(StateResponse.builder()
                .id(UUID.randomUUID())
                .name("Bagmati")
                .countryId(countryId)
                .build())).build());

        mockMvc.perform(post("/api/v2/geo/state-list").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data[0].name").value("Bagmati"));

        verify(geoService).stateList(countryId);
    }

    @Test
    void cityList_returnsSuccess() throws Exception {
        UUID stateId = UUID.randomUUID();
        GeoListRequest request = GeoListRequest.builder().stateId(stateId).build();
        when(geoService.cityList(stateId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(CityResponse.builder()
                .id(UUID.randomUUID())
                .name("Kathmandu")
                .stateId(stateId)
                .build())).build());

        mockMvc.perform(post("/api/v2/geo/city-list").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data[0].name").value("Kathmandu"));

        verify(geoService).cityList(stateId);
    }
}
