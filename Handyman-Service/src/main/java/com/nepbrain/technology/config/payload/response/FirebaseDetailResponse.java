package com.nepbrain.technology.config.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class FirebaseDetailResponse {
private String apiKey; private String appId; private String messagingSenderId; private String projectId; private String storageBucket;
}
