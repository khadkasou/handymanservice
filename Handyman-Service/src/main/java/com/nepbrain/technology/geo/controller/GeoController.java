package com.nepbrain.technology.geo.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.geo.annotation.CustomGeoRestController;
import com.nepbrain.technology.geo.payload.request.GeoListRequest;
import com.nepbrain.technology.geo.service.GeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CustomGeoRestController
@RequiredArgsConstructor
@Tag(name = "Geo")
public class GeoController {

    private final GeoService geoService;

    @PostMapping("/country-list")
    @Operation(summary = "Country list")
    public ResponseEntity<GlobalResponse> countryList() {
        return ResponseEntity.ok().body(geoService.countryList());
    }

    @PostMapping("/state-list")
    @Operation(summary = "State list")
    public ResponseEntity<GlobalResponse> stateList(@RequestBody GeoListRequest request) {
        return ResponseEntity.ok().body(geoService.stateList(request.getCountryId()));
    }

    @PostMapping("/city-list")
    @Operation(summary = "City list")
    public ResponseEntity<GlobalResponse> cityList(@RequestBody GeoListRequest request) {
        return ResponseEntity.ok().body(geoService.cityList(request.getStateId()));
    }
}
