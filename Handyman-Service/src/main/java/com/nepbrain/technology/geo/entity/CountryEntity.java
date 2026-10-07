package com.nepbrain.technology.geo.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity; import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_country")
public class CountryEntity extends BaseAuditEntity {
    private String name; private String code; @Enumerated(EnumType.STRING) private StatusEnum status;
}
