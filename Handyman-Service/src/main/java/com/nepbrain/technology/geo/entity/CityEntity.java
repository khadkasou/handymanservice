package com.nepbrain.technology.geo.entity;

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
@Table(name = "hm_city")
public class CityEntity extends BaseAuditEntity {

    private String name;
    private UUID stateId;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
