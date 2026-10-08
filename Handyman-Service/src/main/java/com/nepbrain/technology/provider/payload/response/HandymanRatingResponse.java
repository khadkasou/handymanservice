package com.nepbrain.technology.provider.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HandymanRatingResponse {

    private UUID id;
    private UUID handymanId;
    private UUID customerId;
    private UUID bookingId;
    private Double rating;
    private String review;
}
