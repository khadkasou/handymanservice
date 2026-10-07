package com.nepbrain.technology.payment.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity; import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_wallet_transaction")
public class WalletTransactionEntity extends BaseAuditEntity {
private UUID userId; private UUID walletId; private BigDecimal amount; private String type; private String activityType; private String datetime;
}
