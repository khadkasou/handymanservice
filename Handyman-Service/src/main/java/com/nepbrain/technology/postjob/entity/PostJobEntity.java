package com.nepbrain.technology.postjob.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_post_job")
public class PostJobEntity extends BaseAuditEntity {

    private String title;

    @Column(length = 4000)
    private String description;

    private UUID customerId;
    private String status;
    private BigDecimal price;
    private String address;
    private UUID categoryId;
}
