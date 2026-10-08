package com.nepbrain.technology.booking.service.impl;

import com.nepbrain.technology.booking.entity.BookingEntity;
import com.nepbrain.technology.booking.entity.BookingStatusEntity;
import com.nepbrain.technology.booking.entity.CouponEntity;
import com.nepbrain.technology.booking.enums.BookingStatusEnum;
import com.nepbrain.technology.booking.enums.ErrorCodeEnum;
import com.nepbrain.technology.booking.payload.request.BookingRequest;
import com.nepbrain.technology.booking.payload.response.BookingResponse;
import com.nepbrain.technology.booking.payload.response.BookingStatusResponse;
import com.nepbrain.technology.booking.payload.response.CouponResponse;
import com.nepbrain.technology.booking.payload.response.LocationResponse;
import com.nepbrain.technology.booking.repository.BookingRepository;
import com.nepbrain.technology.booking.repository.BookingStatusRepository;
import com.nepbrain.technology.booking.repository.CouponRepository;
import com.nepbrain.technology.booking.service.BookingService;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final CouponRepository couponRepository;

    @Override
    public GlobalResponse bookingSave(BookingRequest request) {
        BookingEntity booking = new BookingEntity();
        if (request.getId() != null) {
            booking.setId(request.getId());
        }
        booking.setCustomerId(request.getCustomerId());
        booking.setServiceId(request.getServiceId());
        booking.setProviderId(request.getProviderId());
        booking.setHandymanId(request.getHandymanId());
        booking.setDate(request.getDate());
        booking.setStartAt(request.getStartAt());
        booking.setEndAt(request.getEndAt());
        booking.setAmount(request.getAmount());
        booking.setDiscount(request.getDiscount());
        booking.setTotalAmount(request.getTotalAmount());
        booking.setAddress(request.getAddress());
        booking.setLatitude(request.getLatitude());
        booking.setLongitude(request.getLongitude());
        booking.setBookingSlot(request.getBookingSlot());
        booking.setCouponId(request.getCouponId());
        booking.setQuantity(request.getQuantity());
        booking.setDescription(request.getDescription());
        booking.setStatus(parseStatus(request.getStatus(), BookingStatusEnum.PENDING));
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.booking.save.success", toResponse(bookingRepository.save(booking)));
    }

    @Override
    public GlobalResponse bookingUpdate(BookingRequest request) {
        BookingEntity booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.BKG001.name()));
        if (request.getStatus() != null) {
            booking.setStatus(parseStatus(request.getStatus(), booking.getStatus()));
        }
        if (request.getHandymanId() != null) {
            booking.setHandymanId(request.getHandymanId());
        }
        if (request.getProviderId() != null) {
            booking.setProviderId(request.getProviderId());
        }
        if (request.getStartAt() != null) {
            booking.setStartAt(request.getStartAt());
        }
        if (request.getEndAt() != null) {
            booking.setEndAt(request.getEndAt());
        }
        if (request.getAddress() != null) {
            booking.setAddress(request.getAddress());
        }
        if (request.getLatitude() != null) {
            booking.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            booking.setLongitude(request.getLongitude());
        }
        if (request.getDescription() != null) {
            booking.setDescription(request.getDescription());
        }
        if (request.getTotalAmount() != null) {
            booking.setTotalAmount(request.getTotalAmount());
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.booking.update.success", toResponse(bookingRepository.save(booking)));
    }

    @Override
    public GlobalResponse bookingDetail(UUID id) {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.BKG001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(toResponse(booking));
    }

    @Override
    public GlobalResponse bookingList(UUID customerId) {
        List<BookingEntity> bookings;
        if (customerId != null) {
            bookings = bookingRepository.findByCustomerId(customerId);
        } else {
            bookings = bookingRepository.findAll();
        }
        List<BookingResponse> result = new ArrayList<>();
        for (BookingEntity booking : bookings) {
            result.add(toResponse(booking));
        }
        return GlobalResponseBuilder.buildSuccessResponse(result);
    }

    @Override
    public GlobalResponse bookingStatus() {
        List<BookingStatusResponse> statuses = new ArrayList<>();
        for (BookingStatusEntity status : bookingStatusRepository.findAll()) {
            statuses.add(BookingStatusResponse.builder()
                    .id(status.getId())
                    .value(status.getValue())
                    .label(status.getLabel())
                    .color(status.getColor())
                    .sortOrder(status.getSortOrder())
                    .build());
        }
        return GlobalResponseBuilder.buildSuccessResponse(statuses);
    }

    @Override
    public GlobalResponse getLocation(UUID bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.BKG001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(LocationResponse.builder()
                .bookingId(booking.getId())
                .latitude(booking.getLatitude())
                .longitude(booking.getLongitude())
                .address(booking.getAddress())
                .build());
    }

    @Override
    public GlobalResponse downloadInvoice(UUID bookingId) {
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.BKG001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.booking.invoice.success", "Invoice stub for booking " + bookingId);
    }

    @Override
    public GlobalResponse couponList(UUID serviceId) {
        List<CouponResponse> coupons = new ArrayList<>();
        for (CouponEntity coupon : couponRepository.findByServiceId(serviceId)) {
            String status = coupon.getStatus() == null ? null : coupon.getStatus().name();
            coupons.add(CouponResponse.builder()
                    .id(coupon.getId())
                    .code(coupon.getCode())
                    .description(coupon.getDescription())
                    .discount(coupon.getDiscount())
                    .discountType(coupon.getDiscountType())
                    .serviceId(coupon.getServiceId())
                    .expireDate(coupon.getExpireDate())
                    .status(status)
                    .build());
        }
        return GlobalResponseBuilder.buildSuccessResponse(coupons);
    }

    private BookingStatusEnum parseStatus(String status, BookingStatusEnum fallback) {
        if (status == null) {
            return fallback;
        }
        String normalized = status.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        try {
            return BookingStatusEnum.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new GlobalException(ErrorCodeEnum.BKG003.name());
        }
    }

    private BookingResponse toResponse(BookingEntity booking) {
        String status = booking.getStatus() == null ? null : booking.getStatus().name();
        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomerId())
                .serviceId(booking.getServiceId())
                .providerId(booking.getProviderId())
                .handymanId(booking.getHandymanId())
                .date(booking.getDate())
                .startAt(booking.getStartAt())
                .endAt(booking.getEndAt())
                .amount(booking.getAmount())
                .discount(booking.getDiscount())
                .totalAmount(booking.getTotalAmount())
                .status(status)
                .address(booking.getAddress())
                .latitude(booking.getLatitude())
                .longitude(booking.getLongitude())
                .bookingSlot(booking.getBookingSlot())
                .couponId(booking.getCouponId())
                .quantity(booking.getQuantity())
                .description(booking.getDescription())
                .build();
    }
}
