package com.nepbrain.technology.review.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.review.payload.request.BookingRatingRequest;
import com.nepbrain.technology.review.payload.response.BookingRatingResponse;
import com.nepbrain.technology.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock private ReviewService reviewService;

    @InjectMocks private ReviewController reviewController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void saveBookingRating_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        when(reviewService.saveBookingRating(any(BookingRatingRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(
                BookingRatingResponse.builder()
                .id(id)
                .serviceId(serviceId)
                .rating(4.0)
                .review("Nice")
                .build()).build());

        mockMvc.perform(post("/api/v2/review/save-booking-rating").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(BookingRatingRequest.builder()
                .serviceId(serviceId)
                .rating(4.0)
                .review("Nice")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data.review").value("Nice"));

        verify(reviewService).saveBookingRating(any(BookingRatingRequest.class));
    }

    @Test
    void serviceReviews_returnsOk() throws Exception {
        UUID serviceId = UUID.randomUUID();
        when(reviewService.serviceReviews(serviceId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(BookingRatingResponse.builder()
                .serviceId(serviceId)
                .rating(5.0)
                .review("Excellent")
                .build())).build());

        mockMvc.perform(post("/api/v2/review/service-reviews").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(BookingRatingRequest.builder()
                .serviceId(serviceId)
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].review").value("Excellent"));

        verify(reviewService).serviceReviews(eq(serviceId));
    }

    @Test
    void getUserRatings_withCustomerId() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(reviewService.getUserRatings(customerId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(BookingRatingResponse.builder()
                .customerId(customerId)
                .rating(3.5)
                .build())).build());

        mockMvc.perform(get("/api/v2/review/get-user-ratings").param("customer_id", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customerId").value(customerId.toString()));

        verify(reviewService).getUserRatings(eq(customerId));
    }

    @Test
    void getUserRatings_withoutCustomerId() throws Exception {
        when(reviewService.getUserRatings(null)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of()).build());

        mockMvc.perform(get("/api/v2/review/get-user-ratings")).andExpect(status().isOk());

        verify(reviewService).getUserRatings(isNull());
    }

    @Test
    void deleteBookingRating_returnsSuccessMessage() throws Exception {
        UUID id = UUID.randomUUID();
        when(reviewService.deleteBookingRating(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Rating deleted").build());

        mockMvc.perform(post("/api/v2/review/delete-booking-rating").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("id", id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rating deleted"));

        verify(reviewService).deleteBookingRating(eq(id));
    }
}
