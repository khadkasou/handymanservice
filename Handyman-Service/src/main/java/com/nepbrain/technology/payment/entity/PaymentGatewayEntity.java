package com.nepbrain.technology.payment.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import lombok.*;

import jakarta.persistence.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_payment_gateway")
public class PaymentGatewayEntity extends BaseAuditEntity {

    private String title;
    private String type;
    private Boolean isTest;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private String liveValue;
    private String testValue;
}
