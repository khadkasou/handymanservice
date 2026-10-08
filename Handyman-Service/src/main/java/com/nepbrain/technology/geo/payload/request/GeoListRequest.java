package com.nepbrain.technology.geo.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoListRequest {

    private UUID countryId;
    private UUID stateId;
}
