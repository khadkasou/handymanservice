package com.nepbrain.technology.config.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class DashboardResponse {
private List<Map<String, Object>> category; private List<Map<String, Object>> service; private List<Map<String, Object>> provider; private List<Map<String, Object>> featuredServices; private Map<String, Object> configurations;
}
