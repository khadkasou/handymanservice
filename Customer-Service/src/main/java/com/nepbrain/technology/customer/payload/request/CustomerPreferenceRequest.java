package com.nepbrain.technology.customer.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerPreferenceRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Preference key is required")
    private String preferenceKey;

    @NotBlank(message = "Preference value is required")
    private String preferenceValue;

    private String description;

    private String category;
}

