package com.nepbrain.technology.booking.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class CouponResponse {
private UUID id; private String code; private String description; private BigDecimal discount; private String discountType; private UUID serviceId; private LocalDate expireDate; private String status;
}
