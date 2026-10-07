package com.nepbrain.technology.catalog.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_favourite_service")
public class FavouriteServiceEntity extends BaseAuditEntity {
    private UUID userId; private UUID serviceId;
}
