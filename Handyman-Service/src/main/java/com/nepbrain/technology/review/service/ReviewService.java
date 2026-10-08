package com.nepbrain.technology.review.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.review.payload.request.BookingRatingRequest;

import java.util.UUID;

public interface ReviewService {

    GlobalResponse saveBookingRating(BookingRatingRequest request);

    GlobalResponse serviceReviews(UUID serviceId);

    GlobalResponse getUserRatings(UUID customerId);

    GlobalResponse changeBookingRatingStatus(UUID id, StatusEnum status);

    GlobalResponse deleteBookingRating(UUID id);
}
