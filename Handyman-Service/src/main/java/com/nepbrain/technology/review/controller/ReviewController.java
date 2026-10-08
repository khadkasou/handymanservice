package com.nepbrain.technology.review.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.review.annotation.CustomReviewRestController;
import com.nepbrain.technology.review.payload.request.BookingRatingRequest;
import com.nepbrain.technology.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@CustomReviewRestController
@RequiredArgsConstructor
@Tag(name = "Review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/save-booking-rating")
    @Operation(summary = "Save booking rating")
    public ResponseEntity<GlobalResponse> saveBookingRating(@RequestBody BookingRatingRequest request) {
        return ResponseEntity.ok().body(reviewService.saveBookingRating(request));
    }

    @PostMapping("/service-reviews")
    @Operation(summary = "Service reviews")
    public ResponseEntity<GlobalResponse> serviceReviews(@RequestBody BookingRatingRequest request) {
        return ResponseEntity.ok().body(reviewService.serviceReviews(request.getServiceId()));
    }

    @GetMapping("/get-user-ratings")
    @Operation(summary = "User ratings")
    public ResponseEntity<GlobalResponse> getUserRatings(@RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok().body(reviewService.getUserRatings(customerId));
    }

    @PutMapping("/booking-rating/{id}/status")
    @Operation(summary = "Change booking rating status")
    public ResponseEntity<GlobalResponse> changeBookingRatingStatus(@PathVariable UUID id,
                                                                    @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(reviewService.changeBookingRatingStatus(id, status));
    }

    @PostMapping("/delete-booking-rating")
    @Operation(summary = "Mark booking rating inactive")
    public ResponseEntity<GlobalResponse> deleteBookingRating(@RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok().body(reviewService.deleteBookingRating(body.get("id")));
    }
}
