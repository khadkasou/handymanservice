package com.nepbrain.technology.review.service;

import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.review.entity.BookingRatingEntity;
import com.nepbrain.technology.review.payload.request.BookingRatingRequest;
import com.nepbrain.technology.review.payload.response.BookingRatingResponse;
import com.nepbrain.technology.review.repository.BookingRatingRepository;
import com.nepbrain.technology.review.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private BookingRatingRepository bookingRatingRepository;

    @Spy
    private ModelMapper modelMapper = configuredMapper();

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID serviceId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        customerId = UUID.randomUUID();
    }

    private static ModelMapper configuredMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        return mapper;
    }

    @Test
    void saveBookingRating_success() {
        BookingRatingRequest request = BookingRatingRequest.builder()
                .bookingId(UUID.randomUUID())
                .serviceId(serviceId)
                .customerId(customerId)
                .providerId(UUID.randomUUID())
                .rating(4.5)
                .review("Great work")
                .build();
        when(bookingRatingRepository.save(any(BookingRatingEntity.class))).thenAnswer(invocation -> {
            BookingRatingEntity e = invocation.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        BookingRatingResponse response = reviewService.saveBookingRating(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getRating()).isEqualTo(4.5);
        assertThat(response.getReview()).isEqualTo("Great work");
        verify(bookingRatingRepository).save(any(BookingRatingEntity.class));
    }

    @Test
    void serviceReviews_success() {
        BookingRatingEntity entity = BookingRatingEntity.builder()
                .serviceId(serviceId)
                .customerId(customerId)
                .rating(5.0)
                .review("Excellent")
                .build();
        entity.setId(UUID.randomUUID());
        when(bookingRatingRepository.findByServiceId(serviceId)).thenReturn(List.of(entity));

        List<BookingRatingResponse> result = reviewService.serviceReviews(serviceId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getServiceId()).isEqualTo(serviceId);
        verify(bookingRatingRepository).findByServiceId(serviceId);
    }

    @Test
    void getUserRatings_success() {
        BookingRatingEntity entity = BookingRatingEntity.builder()
                .serviceId(serviceId)
                .customerId(customerId)
                .rating(3.0)
                .build();
        entity.setId(UUID.randomUUID());
        when(bookingRatingRepository.findByCustomerId(customerId)).thenReturn(List.of(entity));

        List<BookingRatingResponse> result = reviewService.getUserRatings(customerId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCustomerId()).isEqualTo(customerId);
        verify(bookingRatingRepository).findByCustomerId(customerId);
    }

    @Test
    void deleteBookingRating_success() {
        UUID id = UUID.randomUUID();
        when(bookingRatingRepository.existsById(id)).thenReturn(true);

        reviewService.deleteBookingRating(id);

        verify(bookingRatingRepository).deleteById(id);
    }

    @Test
    void deleteBookingRating_notFound_throwsGlobalException() {
        UUID id = UUID.randomUUID();
        when(bookingRatingRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.deleteBookingRating(id))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("REV001");
        verify(bookingRatingRepository, never()).deleteById(any());
    }
}
