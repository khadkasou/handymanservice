package com.nepbrain.technology.catalog.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_service")
public class ServiceEntity extends BaseAuditEntity {

    private String name;

    @Column(length = 4000)
    private String description;

    private UUID categoryId;
    private UUID subcategoryId;
    private UUID providerId;
    private BigDecimal price;
    private BigDecimal discount;
    private String duration;
    private String image;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private Boolean isFeatured;
    private String serviceType;
}
