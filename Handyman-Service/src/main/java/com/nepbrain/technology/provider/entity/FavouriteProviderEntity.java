package com.nepbrain.technology.provider.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_favourite_provider")
public class FavouriteProviderEntity extends BaseAuditEntity {
    private UUID userId; private UUID providerId;
}
