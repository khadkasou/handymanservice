package com.nepbrain.technology.auth.entity;

import com.nepbrain.technology.auth.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class RoleEntity extends BaseEntity {

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusEnum status = StatusEnum.ACTIVE;

    /**
     * Many-to-many via {@code role_permission} (same as User-Service).
     * Lazy by default; Auth loads permissions via {@code @EntityGraph} only when issuing tokens.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    @Builder.Default
    private List<PermissionEntity> permissions = new ArrayList<>();
}
