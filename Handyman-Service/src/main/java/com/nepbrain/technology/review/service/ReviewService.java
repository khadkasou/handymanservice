package com.nepbrain.technology.review.service;
import com.nepbrain.technology.review.payload.request.BookingRatingRequest;
import com.nepbrain.technology.review.payload.response.BookingRatingResponse;
import java.util.List; import java.util.UUID;
public interface ReviewService {
    BookingRatingResponse saveBookingRating(BookingRatingRequest request);
    List<BookingRatingResponse> serviceReviews(UUID serviceId);
    List<BookingRatingResponse> getUserRatings(UUID customerId);
    void deleteBookingRating(UUID id);
}
