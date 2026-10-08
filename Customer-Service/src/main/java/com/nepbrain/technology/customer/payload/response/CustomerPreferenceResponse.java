package com.nepbrain.technology.customer.payload.response;

import com.nepbrain.technology.core.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerPreferenceResponse {
    private UUID id;
    private UUID customerId;
    private String customerName;
    private String preferenceKey;
    private String preferenceValue;
    private String description;
    private String category;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
}

