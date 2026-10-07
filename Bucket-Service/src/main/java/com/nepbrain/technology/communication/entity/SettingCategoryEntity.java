package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "setting_category")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SettingCategoryEntity extends BaseAuditEntity {

    @Column(name = "category_code", nullable = false, length = 80)
    private String categoryCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "setting_id", nullable = false)
    private SettingEntity setting;

    @OneToMany(mappedBy = "settingCategory", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SettingDetailEntity> details = new ArrayList<>();
}
