package com.nepbrain.technology.payment.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity; import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_payment_gateway")
public class PaymentGatewayEntity extends BaseAuditEntity {
private String title; private String type; private Boolean isTest; @Enumerated(EnumType.STRING) private StatusEnum status; private String liveValue; private String testValue;
}
