package com.nepbrain.technology.payment.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity; import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_wallet")
public class WalletEntity extends BaseAuditEntity {
private UUID userId; private BigDecimal amount;
}
