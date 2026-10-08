package com.nepbrain.technology.booking.service;

import com.nepbrain.technology.booking.payload.request.BookingRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface BookingService {

    GlobalResponse bookingSave(BookingRequest request);

    GlobalResponse bookingUpdate(BookingRequest request);

    GlobalResponse bookingDetail(UUID id);

    GlobalResponse bookingList(UUID customerId);

    GlobalResponse bookingStatus();

    GlobalResponse getLocation(UUID bookingId);

    GlobalResponse downloadInvoice(UUID bookingId);

    GlobalResponse couponList(UUID serviceId);
}
