package com.nepbrain.technology.booking.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {

    private UUID bookingId;
    private Double latitude;
    private Double longitude;
    private String address;
}
