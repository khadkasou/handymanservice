package com.nepbrain.technology.booking.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import lombok.*;

import jakarta.persistence.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_booking_status")
public class BookingStatusEntity extends BaseAuditEntity {

    private String value;
    private String label;
    private String color;
    private Integer sortOrder;
}
