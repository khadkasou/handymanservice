-- Bucket-Service: per-company channel flags (one row per company)
CREATE TABLE IF NOT EXISTS hrm_company_config (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    company_id UUID NOT NULL,
    enable_email BOOLEAN,
    enable_sms BOOLEAN,
    enable_notification BOOLEAN,
    CONSTRAINT uk_hrm_company_config_company UNIQUE (company_id)
);

-- Bucket-Service: per-company email / SMTP (exactly one row per company)
CREATE TABLE IF NOT EXISTS hrm_company_email_config (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    company_id UUID NOT NULL,
    from_name VARCHAR(200),
    from_email VARCHAR(255),
    smtp_host VARCHAR(255),
    smtp_port INTEGER,
    smtp_username VARCHAR(255),
    smtp_password VARCHAR(500),
    smtp_use_tls BOOLEAN,
    CONSTRAINT uk_hrm_company_email_config_company UNIQUE (company_id)
);

-- Bucket-Service: per-company SMS config (one row per company + provider; one active at a time)
CREATE TABLE IF NOT EXISTS hrm_company_sms_config (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    company_id UUID NOT NULL,
    provider VARCHAR(40) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    token VARCHAR(500),
    sms_from VARCHAR(100),
    api_url VARCHAR(500),
    auth_field_name VARCHAR(100),
    CONSTRAINT uk_hrm_company_sms_config_company_provider UNIQUE (company_id, provider)
);
