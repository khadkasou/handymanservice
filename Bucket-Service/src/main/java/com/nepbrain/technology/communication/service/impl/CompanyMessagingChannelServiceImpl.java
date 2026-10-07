package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.CompanyConfigEntity;
import com.nepbrain.technology.communication.entity.CompanyEmailConfigEntity;
import com.nepbrain.technology.communication.entity.CompanySmsConfigEntity;
import com.nepbrain.technology.communication.repository.CompanyConfigRepository;
import com.nepbrain.technology.communication.repository.CompanyEmailConfigRepository;
import com.nepbrain.technology.communication.repository.CompanySmsConfigRepository;
import com.nepbrain.technology.communication.service.CompanyMessagingChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyMessagingChannelServiceImpl implements CompanyMessagingChannelService {

    private final CompanyConfigRepository companyConfigRepository;
    private final CompanyEmailConfigRepository emailConfigRepository;
    private final CompanySmsConfigRepository smsConfigRepository;

    @Override
    public Optional<CompanyEmailConfigEntity> resolveCompanyEmailChannel(UUID companyId) {
        if (companyId == null) {
            return Optional.empty();
        }
        CompanyConfigEntity config = companyConfigRepository.findByCompanyId(companyId).orElse(null);
        if (config == null || !Boolean.TRUE.equals(config.getEnableEmail())) {
            log.debug("Company {} SMTP not enabled — platform default email will be used", companyId);
            return Optional.empty();
        }
        Optional<CompanyEmailConfigEntity> email = emailConfigRepository.findByCompanyId(companyId)
                .filter(this::hasSmtpBasics);
        if (email.isEmpty()) {
            log.warn("Company {} has enableEmail=true but SMTP config is incomplete — falling back to platform default email",
                    companyId);
        }
        return email;
    }

    @Override
    public Optional<CompanySmsConfigEntity> resolveCompanySmsChannel(UUID companyId) {
        if (companyId == null) {
            return Optional.empty();
        }
        CompanyConfigEntity config = companyConfigRepository.findByCompanyId(companyId).orElse(null);
        if (config == null || !Boolean.TRUE.equals(config.getEnableSms())) {
            return Optional.empty();
        }
        Optional<CompanySmsConfigEntity> sms = smsConfigRepository.findByCompanyId(companyId).stream()
                .filter(CompanySmsConfigEntity::isActive)
                .filter(this::hasSmsBasics)
                .findFirst();
        if (sms.isEmpty()) {
            log.warn("Company {} has enableSms=true but no active SMS provider config — falling back to platform SMS",
                    companyId);
        }
        return sms;
    }

    private boolean hasSmtpBasics(CompanyEmailConfigEntity email) {
        return email != null
                && StringUtils.hasText(email.getSmtpHost())
                && StringUtils.hasText(email.getFromEmail())
                && email.getSmtpPort() != null
                && email.getSmtpPort() > 0;
    }

    private boolean hasSmsBasics(CompanySmsConfigEntity sms) {
        return sms != null
                && sms.getProvider() != null
                && StringUtils.hasText(sms.getToken())
                && StringUtils.hasText(sms.getApiUrl());
    }
}
