package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.communication.enums.SmsProviderEnum;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Per-company SMS provider credentials — one row per ({@code company_id}, {@code provider}).
 * Exactly one row per company may be {@code active} at a time.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "hrm_company_sms_config",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_hrm_company_sms_config_company_provider",
                columnNames = {"company_id", "provider"}))
public class CompanySmsConfigEntity extends BaseAuditEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 40)
    private SmsProviderEnum provider;

    /** Only one SMS provider row may be active per company. */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = false;

    @Column(name = "token", length = 500)
    private String token;

    @Column(name = "sms_from", length = 100)
    private String smsFrom;

    @Column(name = "api_url", length = 500)
    private String apiUrl;

    @Column(name = "auth_field_name", length = 100)
    private String authFieldName;
}
