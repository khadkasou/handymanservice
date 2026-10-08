package com.nepbrain.technology.provider.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HandymanRatingRequest {

    private UUID id;
    private UUID handymanId;
    private UUID customerId;
    private UUID bookingId;
    private Double rating;
    private String review;
}
