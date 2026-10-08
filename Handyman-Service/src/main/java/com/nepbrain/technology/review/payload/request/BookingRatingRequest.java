package com.nepbrain.technology.review.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRatingRequest {

    private UUID id;
    private UUID bookingId;
    private UUID serviceId;
    private UUID customerId;
    private UUID providerId;
    private Double rating;
    private String review;
}
