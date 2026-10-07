package com.nepbrain.technology.geo.controller;
import com.nepbrain.technology.geo.annotation.CustomGeoRestController;
import com.nepbrain.technology.geo.payload.request.GeoListRequest;
import com.nepbrain.technology.geo.service.GeoService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;

@CustomGeoRestController @RequiredArgsConstructor
@Tag(name = "Geo", description = "Country / state / city APIs")
public class GeoController {
    private final GeoService geoService;
    @PostMapping("/country-list") public ResponseEntity<GlobalResponse> countryList() {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("COUNTRY_LIST", geoService.countryList()));
    }
    @PostMapping("/state-list") public ResponseEntity<GlobalResponse> stateList(@RequestBody GeoListRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("STATE_LIST", geoService.stateList(request.getCountryId())));
    }
    @PostMapping("/city-list") public ResponseEntity<GlobalResponse> cityList(@RequestBody GeoListRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CITY_LIST", geoService.cityList(request.getStateId())));
    }
}
