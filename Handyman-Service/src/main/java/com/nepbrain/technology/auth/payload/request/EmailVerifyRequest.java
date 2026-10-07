package com.nepbrain.technology.auth.payload.request;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class EmailVerifyRequest {
private String email; private String verificationId; private String otpCode;
}
