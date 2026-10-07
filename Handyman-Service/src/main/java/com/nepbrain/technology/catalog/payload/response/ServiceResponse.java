package com.nepbrain.technology.catalog.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class ServiceResponse {
private UUID id; private String name; private String description; private UUID categoryId; private UUID subcategoryId; private UUID providerId; private BigDecimal price; private BigDecimal discount; private String duration; private String image; private String status; private Boolean isFeatured; private String serviceType;
}
