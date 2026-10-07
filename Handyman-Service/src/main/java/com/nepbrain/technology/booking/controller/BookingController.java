package com.nepbrain.technology.booking.controller;
import com.nepbrain.technology.booking.annotation.CustomBookingRestController;
import com.nepbrain.technology.booking.payload.request.BookingRequest;
import com.nepbrain.technology.booking.service.BookingService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.Map; import java.util.UUID;

@CustomBookingRestController @RequiredArgsConstructor
@Tag(name = "Booking", description = "Booking APIs")
public class BookingController {
    private final BookingService bookingService;
    @PostMapping("/booking-save") public ResponseEntity<GlobalResponse> bookingSave(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("BOOKING_SAVE", bookingService.bookingSave(request)));
    }
    @PostMapping("/booking-update") public ResponseEntity<GlobalResponse> bookingUpdate(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("BOOKING_UPDATE", bookingService.bookingUpdate(request)));
    }
    @PostMapping("/booking-detail") public ResponseEntity<GlobalResponse> bookingDetail(@RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("BOOKING_DETAIL", bookingService.bookingDetail(body.get("id"))));
    }
    @GetMapping("/booking-list") public ResponseEntity<GlobalResponse> bookingList(@RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("BOOKING_LIST", bookingService.bookingList(customerId)));
    }
    @GetMapping("/booking-status") public ResponseEntity<GlobalResponse> bookingStatus() {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("BOOKING_STATUS", bookingService.bookingStatus()));
    }
    @GetMapping("/get-location") public ResponseEntity<GlobalResponse> getLocation(@RequestParam("booking_id") UUID bookingId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("GET_LOCATION", bookingService.getLocation(bookingId)));
    }
    @PostMapping("/download-invoice") public ResponseEntity<GlobalResponse> downloadInvoice(@RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DOWNLOAD_INVOICE", bookingService.downloadInvoice(body.get("id"))));
    }
    @GetMapping("/coupon-list") public ResponseEntity<GlobalResponse> couponList(@RequestParam("service_id") UUID serviceId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("COUPON_LIST", bookingService.couponList(serviceId)));
    }
}
