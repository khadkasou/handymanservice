package com.nepbrain.technology.booking.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class BookingStatusResponse {
private UUID id; private String value; private String label; private String color; private Integer sortOrder;
}
