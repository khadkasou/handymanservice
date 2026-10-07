package com.nepbrain.technology.auth.entity;

import com.nepbrain.technology.auth.enums.StatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permission")
public class PermissionEntity extends BaseEntity {

    private String name;
    private String code;
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusEnum status = StatusEnum.ACTIVE;

}
