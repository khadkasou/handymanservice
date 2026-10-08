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
@Table(name = "hm_favourite_service")
public class FavouriteServiceEntity extends BaseAuditEntity {

    private UUID userId;
    private UUID serviceId;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
