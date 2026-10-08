package com.nepbrain.technology.booking.controller;

import com.nepbrain.technology.booking.annotation.CustomBookingRestController;
import com.nepbrain.technology.booking.payload.request.BookingRequest;
import com.nepbrain.technology.booking.service.BookingService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@CustomBookingRestController
@RequiredArgsConstructor
@Tag(name = "Booking")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/booking-save")
    @Operation(summary = "Save booking")
    public ResponseEntity<GlobalResponse> bookingSave(@RequestBody BookingRequest request) {
        return ResponseEntity.ok().body(bookingService.bookingSave(request));
    }

    @PostMapping("/booking-update")
    @Operation(summary = "Update booking")
    public ResponseEntity<GlobalResponse> bookingUpdate(@RequestBody BookingRequest request) {
        return ResponseEntity.ok().body(bookingService.bookingUpdate(request));
    }

    @PostMapping("/booking-detail")
    @Operation(summary = "Booking detail")
    public ResponseEntity<GlobalResponse> bookingDetail(@RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok().body(bookingService.bookingDetail(body.get("id")));
    }

    @GetMapping("/booking-list")
    @Operation(summary = "Booking list")
    public ResponseEntity<GlobalResponse> bookingList(@RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok().body(bookingService.bookingList(customerId));
    }

    @GetMapping("/booking-status")
    @Operation(summary = "Booking status list")
    public ResponseEntity<GlobalResponse> bookingStatus() {
        return ResponseEntity.ok().body(bookingService.bookingStatus());
    }

    @GetMapping("/get-location")
    @Operation(summary = "Booking location")
    public ResponseEntity<GlobalResponse> getLocation(@RequestParam("booking_id") UUID bookingId) {
        return ResponseEntity.ok().body(bookingService.getLocation(bookingId));
    }

    @PostMapping("/download-invoice")
    @Operation(summary = "Download invoice")
    public ResponseEntity<GlobalResponse> downloadInvoice(@RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok().body(bookingService.downloadInvoice(body.get("id")));
    }

    @GetMapping("/coupon-list")
    @Operation(summary = "Coupon list")
    public ResponseEntity<GlobalResponse> couponList(@RequestParam("service_id") UUID serviceId) {
        return ResponseEntity.ok().body(bookingService.couponList(serviceId));
    }
}
