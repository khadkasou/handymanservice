package com.nepbrain.technology.booking.payload.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequest {

    private UUID id;
    private UUID customerId;
    private UUID serviceId;
    private UUID providerId;
    private UUID handymanId;
    private LocalDate date;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private String status;
    private String address;
    private Double latitude;
    private Double longitude;
    private String bookingSlot;
    private UUID couponId;
    private Integer quantity;
    private String description;
}
