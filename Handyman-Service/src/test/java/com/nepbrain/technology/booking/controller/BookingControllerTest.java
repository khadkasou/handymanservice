package com.nepbrain.technology.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.booking.payload.request.BookingRequest;
import com.nepbrain.technology.booking.payload.response.BookingResponse;
import com.nepbrain.technology.booking.payload.response.BookingStatusResponse;
import com.nepbrain.technology.booking.payload.response.CouponResponse;
import com.nepbrain.technology.booking.payload.response.LocationResponse;
import com.nepbrain.technology.booking.service.BookingService;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void bookingSave_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookingService.bookingSave(any(BookingRequest.class))).thenReturn(BookingResponse.builder()
                .id(id)
                .status("PENDING")
                .totalAmount(new BigDecimal("1000"))
                .build());

        mockMvc.perform(post("/api/v2/booking/booking-save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingRequest.builder()
                                .customerId(UUID.randomUUID())
                                .address("Kathmandu")
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.code").value("BOOKING_SAVE"))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(bookingService).bookingSave(any(BookingRequest.class));
    }

    @Test
    void bookingUpdate_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookingService.bookingUpdate(any(BookingRequest.class))).thenReturn(BookingResponse.builder()
                .id(id)
                .status("COMPLETED")
                .build());

        mockMvc.perform(post("/api/v2/booking/booking-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingRequest.builder()
                                .id(id)
                                .status("COMPLETED")
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BOOKING_UPDATE"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        verify(bookingService).bookingUpdate(any(BookingRequest.class));
    }

    @Test
    void bookingDetail_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookingService.bookingDetail(id)).thenReturn(BookingResponse.builder()
                .id(id)
                .status("ACCEPT")
                .build());

        mockMvc.perform(post("/api/v2/booking/booking-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("id", id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BOOKING_DETAIL"))
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(bookingService).bookingDetail(eq(id));
    }

    @Test
    void bookingList_withCustomerId() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(bookingService.bookingList(customerId)).thenReturn(List.of(BookingResponse.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .status("PENDING")
                .build()));

        mockMvc.perform(get("/api/v2/booking/booking-list").param("customer_id", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BOOKING_LIST"))
                .andExpect(jsonPath("$.data[0].customerId").value(customerId.toString()));

        verify(bookingService).bookingList(eq(customerId));
    }

    @Test
    void bookingList_withoutCustomerId() throws Exception {
        when(bookingService.bookingList(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/booking/booking-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BOOKING_LIST"));

        verify(bookingService).bookingList(isNull());
    }

    @Test
    void bookingStatus_returnsOk() throws Exception {
        when(bookingService.bookingStatus()).thenReturn(List.of(BookingStatusResponse.builder()
                .value("pending")
                .label("Pending")
                .build()));

        mockMvc.perform(get("/api/v2/booking/booking-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BOOKING_STATUS"))
                .andExpect(jsonPath("$.data[0].value").value("pending"));

        verify(bookingService).bookingStatus();
    }

    @Test
    void getLocation_returnsOk() throws Exception {
        UUID bookingId = UUID.randomUUID();
        when(bookingService.getLocation(bookingId)).thenReturn(LocationResponse.builder()
                .bookingId(bookingId)
                .latitude(27.7)
                .longitude(85.3)
                .address("Patan")
                .build());

        mockMvc.perform(get("/api/v2/booking/get-location").param("booking_id", bookingId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("GET_LOCATION"))
                .andExpect(jsonPath("$.data.address").value("Patan"))
                .andExpect(jsonPath("$.data.latitude").value(27.7));

        verify(bookingService).getLocation(eq(bookingId));
    }

    @Test
    void downloadInvoice_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookingService.downloadInvoice(id)).thenReturn("Invoice stub for booking " + id);

        mockMvc.perform(post("/api/v2/booking/download-invoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("id", id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOWNLOAD_INVOICE"))
                .andExpect(jsonPath("$.data").value("Invoice stub for booking " + id));

        verify(bookingService).downloadInvoice(eq(id));
    }

    @Test
    void couponList_returnsOk() throws Exception {
        UUID serviceId = UUID.randomUUID();
        when(bookingService.couponList(serviceId)).thenReturn(List.of(CouponResponse.builder()
                .code("SAVE10")
                .serviceId(serviceId)
                .status("ACTIVE")
                .build()));

        mockMvc.perform(get("/api/v2/booking/coupon-list").param("service_id", serviceId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("COUPON_LIST"))
                .andExpect(jsonPath("$.data[0].code").value("SAVE10"));

        verify(bookingService).couponList(eq(serviceId));
    }
}
