package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.entity.CompanyEmailConfigEntity;
import com.nepbrain.technology.communication.entity.CompanySmsConfigEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolves whether a company should use its own email/SMS channel for outbound messages.
 */
public interface CompanyMessagingChannelService {

    /**
     * When company has {@code enableEmail=true} and SMTP config exists, return that config.
     * Empty means use platform default (AWS SES / Mailgun settings).
     */
    Optional<CompanyEmailConfigEntity> resolveCompanyEmailChannel(UUID companyId);

    /**
     * When company has {@code enableSms=true} and an active SMS provider row exists, return it.
     * Empty means use platform default SMS.
     */
    Optional<CompanySmsConfigEntity> resolveCompanySmsChannel(UUID companyId);
}
