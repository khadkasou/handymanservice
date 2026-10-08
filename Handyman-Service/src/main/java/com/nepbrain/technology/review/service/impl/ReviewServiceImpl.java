package com.nepbrain.technology.review.service.impl;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.review.entity.BookingRatingEntity;
import com.nepbrain.technology.review.enums.ErrorCodeEnum;
import com.nepbrain.technology.review.payload.request.BookingRatingRequest;
import com.nepbrain.technology.review.payload.response.BookingRatingResponse;
import com.nepbrain.technology.review.repository.BookingRatingRepository;
import com.nepbrain.technology.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final BookingRatingRepository bookingRatingRepository;

    @Override
    public GlobalResponse saveBookingRating(BookingRatingRequest request) {
        BookingRatingEntity rating = new BookingRatingEntity();
        if (request.getId() != null) {
            rating.setId(request.getId());
        }
        rating.setBookingId(request.getBookingId());
        rating.setServiceId(request.getServiceId());
        rating.setCustomerId(request.getCustomerId());
        rating.setProviderId(request.getProviderId());
        rating.setRating(request.getRating());
        rating.setReview(request.getReview());
        if (rating.getStatus() == null) {
            rating.setStatus(StatusEnum.ACTIVE);
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.review.saved.success", toResponse(bookingRatingRepository.save(rating)));
    }

    @Override
    public GlobalResponse serviceReviews(UUID serviceId) {
        List<BookingRatingResponse> reviews = new ArrayList<>();
        for (BookingRatingEntity rating : bookingRatingRepository.findByServiceId(serviceId)) {
            if (rating.getStatus() == StatusEnum.INACTIVE) {
                continue;
            }
            reviews.add(toResponse(rating));
        }
        return GlobalResponseBuilder.buildSuccessResponse(reviews);
    }

    @Override
    public GlobalResponse getUserRatings(UUID customerId) {
        List<BookingRatingResponse> reviews = new ArrayList<>();
        for (BookingRatingEntity rating : bookingRatingRepository.findByCustomerId(customerId)) {
            if (rating.getStatus() == StatusEnum.INACTIVE) {
                continue;
            }
            reviews.add(toResponse(rating));
        }
        return GlobalResponseBuilder.buildSuccessResponse(reviews);
    }

    @Override
    public GlobalResponse changeBookingRatingStatus(UUID id, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.REV002.name());
        }
        BookingRatingEntity rating = bookingRatingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.REV001.name(), HttpStatus.BAD_REQUEST));
        rating.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.review.status.success", toResponse(bookingRatingRepository.save(rating)));
    }

    @Override
    public GlobalResponse deleteBookingRating(UUID id) {
        return changeBookingRatingStatus(id, StatusEnum.INACTIVE);
    }

    private BookingRatingResponse toResponse(BookingRatingEntity rating) {
        return BookingRatingResponse.builder()
                .id(rating.getId())
                .bookingId(rating.getBookingId())
                .serviceId(rating.getServiceId())
                .customerId(rating.getCustomerId())
                .providerId(rating.getProviderId())
                .rating(rating.getRating())
                .review(rating.getReview())
                .build();
    }
}
