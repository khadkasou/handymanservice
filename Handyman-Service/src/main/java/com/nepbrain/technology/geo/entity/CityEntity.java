package com.nepbrain.technology.geo.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity; import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_city")
public class CityEntity extends BaseAuditEntity {
    private String name; private UUID stateId; @Enumerated(EnumType.STRING) private StatusEnum status;
}
