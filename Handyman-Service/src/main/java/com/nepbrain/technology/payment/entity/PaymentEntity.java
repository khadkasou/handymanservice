package com.nepbrain.technology.payment.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_payment")
public class PaymentEntity extends BaseAuditEntity {

    private UUID bookingId;
    private UUID customerId;
    private BigDecimal amount;
    private String paymentType;
    private String txnId;
    private String paymentStatus;
    private String datetime;
}
