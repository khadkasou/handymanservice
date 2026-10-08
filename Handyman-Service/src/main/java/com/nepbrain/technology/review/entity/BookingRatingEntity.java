package com.nepbrain.technology.review.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_booking_rating")
public class BookingRatingEntity extends BaseAuditEntity {

    private UUID bookingId;
    private UUID serviceId;
    private UUID customerId;
    private UUID providerId;
    private Double rating;

    @Column(length = 2000)
    private String review;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
