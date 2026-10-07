package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "setting")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SettingEntity extends BaseAuditEntity {

    @Column(name = "setting_code", nullable = false, unique = true, length = 50)
    private String settingCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "setting", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SettingCategoryEntity> categories = new ArrayList<>();
}
