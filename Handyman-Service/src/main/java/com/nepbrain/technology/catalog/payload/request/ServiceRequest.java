package com.nepbrain.technology.catalog.payload.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequest {

    private UUID id;
    private String name;
    private String description;
    private UUID categoryId;
    private UUID subcategoryId;
    private UUID providerId;
    private BigDecimal price;
    private BigDecimal discount;
    private String duration;
    private String image;
    private String serviceType;
    private Boolean isFeatured;
    private UUID customerId;
}
