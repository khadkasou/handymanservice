package com.nepbrain.technology.booking.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity; import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_coupon")
public class CouponEntity extends BaseAuditEntity {
    private String code; private String description; private BigDecimal discount;
    private String discountType; private UUID serviceId; private LocalDate expireDate;
    @Enumerated(EnumType.STRING) private StatusEnum status;
}
