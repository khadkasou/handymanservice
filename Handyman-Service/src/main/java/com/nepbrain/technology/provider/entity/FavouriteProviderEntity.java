package com.nepbrain.technology.provider.entity;

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
@Table(name = "hm_favourite_provider")
public class FavouriteProviderEntity extends BaseAuditEntity {

    private UUID userId;
    private UUID providerId;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
