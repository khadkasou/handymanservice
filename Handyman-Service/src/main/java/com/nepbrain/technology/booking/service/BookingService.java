package com.nepbrain.technology.booking.service;
import com.nepbrain.technology.booking.payload.request.BookingRequest;
import com.nepbrain.technology.booking.payload.response.*;
import java.util.List; import java.util.UUID;
public interface BookingService {
    BookingResponse bookingSave(BookingRequest request);
    BookingResponse bookingUpdate(BookingRequest request);
    BookingResponse bookingDetail(UUID id);
    List<BookingResponse> bookingList(UUID customerId);
    List<BookingStatusResponse> bookingStatus();
    LocationResponse getLocation(UUID bookingId);
    String downloadInvoice(UUID bookingId);
    List<CouponResponse> couponList(UUID serviceId);
}
