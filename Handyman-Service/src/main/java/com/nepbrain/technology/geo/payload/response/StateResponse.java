package com.nepbrain.technology.geo.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StateResponse {

    private UUID id;
    private String name;
    private UUID countryId;
}
