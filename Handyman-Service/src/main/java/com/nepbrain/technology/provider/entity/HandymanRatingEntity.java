package com.nepbrain.technology.provider.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_handyman_rating")
public class HandymanRatingEntity extends BaseAuditEntity {
    private UUID handymanId; private UUID customerId; private UUID bookingId;
    private Double rating; @Column(length = 2000) private String review;
}
