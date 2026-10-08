package com.nepbrain.technology.catalog.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_category")
public class CategoryEntity extends BaseAuditEntity {

    private String name;

    @Column(length = 2000)
    private String description;

    private String image;
    private UUID parentId;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private Boolean isFeatured;
}
