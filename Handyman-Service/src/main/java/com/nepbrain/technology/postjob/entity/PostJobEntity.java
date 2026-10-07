package com.nepbrain.technology.postjob.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity; import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_post_job")
public class PostJobEntity extends BaseAuditEntity {
    private String title; @Column(length = 4000) private String description;
    private UUID customerId; @Enumerated(EnumType.STRING) private StatusEnum status;
    private BigDecimal price; private String address; private UUID categoryId;
}
