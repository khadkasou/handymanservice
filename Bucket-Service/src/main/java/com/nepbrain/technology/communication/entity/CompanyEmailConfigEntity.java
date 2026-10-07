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
 * Per-company email / SMTP settings. Exactly one row per {@code company_id}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hrm_company_email_config")
public class CompanyEmailConfigEntity extends BaseAuditEntity {

    @Column(name = "company_id", nullable = false, unique = true)
    private UUID companyId;

    @Column(name = "from_name", length = 200)
    private String fromName;

    @Column(name = "from_email", length = 255)
    private String fromEmail;

    @Column(name = "smtp_host", length = 255)
    private String smtpHost;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_username", length = 255)
    private String smtpUsername;

    @Column(name = "smtp_password", length = 500)
    private String smtpPassword;

    @Column(name = "smtp_use_tls")
    private Boolean smtpUseTls;
}
