package com.nepbrain.technology.catalog.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class CategoryResponse {
private UUID id; private String name; private String description; private String image; private UUID parentId; private String status; private Boolean isFeatured;
}
