package com.nepbrain.technology.customer.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_preference", uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "preference_key"}))
public class CustomerPreferenceEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(nullable = false)
    private String preferenceKey; // e.g., "language", "currency", "notification_email", "notification_sms", "theme", etc.

    @Column(columnDefinition = "TEXT", nullable = false)
    private String preferenceValue; // The value for this preference

    @Column(columnDefinition = "TEXT")
    private String description; // Description of what this preference is for

    private String category; // e.g., "communication", "display", "billing", "shipping", etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
    private StatusEnum status = StatusEnum.ACTIVE;
}

