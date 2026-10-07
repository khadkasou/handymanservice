package com.nepbrain.technology.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID createdBy;

    private UUID lastModifiedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    @PreUpdate
    public void preUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }


}
