package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Per-company messaging channel switches. One row per {@code company_id}.
 * Email SMTP lives in {@link CompanyEmailConfigEntity}; SMS providers in {@link CompanySmsConfigEntity}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hrm_company_config")
public class CompanyConfigEntity extends BaseAuditEntity {

    @Column(name = "company_id", nullable = false, unique = true)
    private UUID companyId;

    @Column(name = "enable_email")
    private Boolean enableEmail;

    @Column(name = "enable_sms")
    private Boolean enableSms;

    @Column(name = "enable_notification")
    private Boolean enableNotification;
}
