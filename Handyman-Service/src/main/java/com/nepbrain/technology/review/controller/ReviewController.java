package com.nepbrain.technology.review.controller;
import com.nepbrain.technology.review.annotation.CustomReviewRestController;
import com.nepbrain.technology.review.payload.request.BookingRatingRequest;
import com.nepbrain.technology.review.service.ReviewService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.Map; import java.util.UUID;

@CustomReviewRestController @RequiredArgsConstructor
@Tag(name = "Review", description = "Booking rating APIs")
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping("/save-booking-rating") public ResponseEntity<GlobalResponse> saveBookingRating(@RequestBody BookingRatingRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SAVE_BOOKING_RATING", reviewService.saveBookingRating(request)));
    }
    @PostMapping("/service-reviews") public ResponseEntity<GlobalResponse> serviceReviews(@RequestBody BookingRatingRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SERVICE_REVIEWS", reviewService.serviceReviews(request.getServiceId())));
    }
    @GetMapping("/get-user-ratings") public ResponseEntity<GlobalResponse> getUserRatings(@RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("GET_USER_RATINGS", reviewService.getUserRatings(customerId)));
    }
    @PostMapping("/delete-booking-rating") public ResponseEntity<GlobalResponse> deleteBookingRating(@RequestBody Map<String, UUID> body) {
        reviewService.deleteBookingRating(body.get("id"));
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DELETE_BOOKING_RATING", "Rating deleted"));
    }
}
