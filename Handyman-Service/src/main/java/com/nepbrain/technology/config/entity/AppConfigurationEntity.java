package com.nepbrain.technology.config.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import lombok.*;

import jakarta.persistence.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_app_configuration")
public class AppConfigurationEntity extends BaseAuditEntity {

    @Column(name = "config_key", unique = true, nullable = false)
    private String key;

    @Column(length = 8000)
    private String value;

    private String type;
}
