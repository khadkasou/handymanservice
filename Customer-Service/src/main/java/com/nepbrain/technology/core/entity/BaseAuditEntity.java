package com.nepbrain.technology.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.nepbrain.technology.core.config.security.CurrentUser.getUser;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Version
    private Long version;

    /** Automatically set when entity is first persisted */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Automatically updated whenever entity changes */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastModifiedAt;

    /** Automatically populated by Spring Data AuditorAware */
    @CreatedBy
    @Column(updatable = false)
    private UUID createdBy;

    @LastModifiedBy
    private UUID lastModifiedBy;

    /** In case auditing is not enabled or you want fallback logic */
    @PrePersist
    public void prePersist() {
        if (createdBy == null) createdBy = getUser();
        if (lastModifiedBy == null) lastModifiedBy = getUser();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedBy = getUser();
    }
}
