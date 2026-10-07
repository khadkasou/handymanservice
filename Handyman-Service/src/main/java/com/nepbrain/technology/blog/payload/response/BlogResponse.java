package com.nepbrain.technology.blog.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class BlogResponse {
private UUID id; private String title; private String description; private String image; private Boolean isFeatured; private String status; private String authorName;
}
