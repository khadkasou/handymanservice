package com.nepbrain.technology.payment.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class BankResponse {
private UUID id; private UUID userId; private String bankName; private String branchName; private String accountNumber; private String ifscNo; private String accountHolderName; private Boolean isDefault;
}
