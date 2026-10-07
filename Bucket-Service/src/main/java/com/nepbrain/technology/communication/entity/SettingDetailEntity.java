package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "setting_detail")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SettingDetailEntity extends BaseAuditEntity {

    @Column(name = "detail_key", length = 120)
    private String key;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "display_name", length = 200)
    private String displayName;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "setting_category_id", nullable = false)
    private SettingCategoryEntity settingCategory;
}
