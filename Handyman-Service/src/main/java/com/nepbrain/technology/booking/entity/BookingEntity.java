package com.nepbrain.technology.booking.entity;

import com.nepbrain.technology.booking.enums.BookingStatusEnum;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_booking")
public class BookingEntity extends BaseAuditEntity {

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

    @Enumerated(EnumType.STRING)
    private BookingStatusEnum status;

    private String address;
    private Double latitude;
    private Double longitude;
    private String bookingSlot;
    private UUID couponId;
    private Integer quantity;

    @Column(length = 2000)
    private String description;
}
