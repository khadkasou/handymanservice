package com.nepbrain.technology.review.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRatingResponse {

    private UUID id;
    private UUID bookingId;
    private UUID serviceId;
    private UUID customerId;
    private UUID providerId;
    private Double rating;
    private String review;
}
