package com.nepbrain.technology.geo.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity; import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_state")
public class StateEntity extends BaseAuditEntity {
    private String name; private UUID countryId; @Enumerated(EnumType.STRING) private StatusEnum status;
}
