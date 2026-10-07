package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.CompanyConfigEntity;
import com.nepbrain.technology.communication.entity.CompanyEmailConfigEntity;
import com.nepbrain.technology.communication.entity.CompanySmsConfigEntity;
import com.nepbrain.technology.communication.enums.SmsProviderEnum;
import com.nepbrain.technology.communication.payload.request.CompanyMessagingConfigRequest;
import com.nepbrain.technology.communication.payload.request.TestEmailRequest;
import com.nepbrain.technology.communication.payload.request.TestSmsRequest;
import com.nepbrain.technology.communication.payload.response.CompanyMessagingConfigResponse;
import com.nepbrain.technology.communication.payload.response.TestMessageResultResponse;
import com.nepbrain.technology.communication.repository.CompanyConfigRepository;
import com.nepbrain.technology.communication.repository.CompanyEmailConfigRepository;
import com.nepbrain.technology.communication.repository.CompanySmsConfigRepository;
import com.nepbrain.technology.communication.service.CompanyMessagingConfigService;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyMessagingConfigServiceImpl implements CompanyMessagingConfigService {

    private static final String SPARROW_DEFAULT_URL = "http://api.sparrowsms.com/v2/sms/";
    private static final String AAKASH_DEFAULT_URL = "https://sms.aakashsms.com/sms/v3/send/";

    private final CompanyConfigRepository companyConfigRepository;
    private final CompanyEmailConfigRepository emailConfigRepository;
    private final CompanySmsConfigRepository smsConfigRepository;

    @Override
    @Transactional(readOnly = true)
    public CompanyMessagingConfigResponse getByCompanyId(UUID companyId) {
        requireCompanyId(companyId);
        CompanyConfigEntity config = companyConfigRepository.findByCompanyId(companyId).orElse(null);
        CompanyEmailConfigEntity email = emailConfigRepository.findByCompanyId(companyId).orElse(null);
        Map<SmsProviderEnum, CompanySmsConfigEntity> smsByProvider =
                indexSms(smsConfigRepository.findByCompanyId(companyId));
        if (config == null && email == null && smsByProvider.isEmpty()) {
            return emptyResponse(companyId);
        }
        return toResponse(companyId, config, email, smsByProvider);
    }

    @Override
    public CompanyMessagingConfigResponse save(UUID companyId, CompanyMessagingConfigRequest request) {
        requireCompanyId(companyId);
        if (request == null) {
            throw new GlobalException("MESSAGING_CONFIG_REQUIRED", HttpStatus.BAD_REQUEST);
        }

        CompanyConfigEntity config = companyConfigRepository.findByCompanyId(companyId)
                .orElseGet(() -> CompanyConfigEntity.builder().companyId(companyId).build());
        applyChannelFlags(request, config);
        config.setCompanyId(companyId);
        config = companyConfigRepository.save(config);

        CompanyEmailConfigEntity email = emailConfigRepository.findByCompanyId(companyId)
                .orElseGet(() -> CompanyEmailConfigEntity.builder().companyId(companyId).build());
        applyEmail(request, email);
        email.setCompanyId(companyId);
        email = emailConfigRepository.save(email);

        SmsProviderEnum activeProvider = request.getSmsProvider() != null
                ? request.getSmsProvider()
                : SmsProviderEnum.SPARROW;

        upsertSms(companyId, SmsProviderEnum.SPARROW,
                request.getSparrowSmsToken(),
                request.getSparrowSmsFrom(),
                request.getSparrowSmsApiUrl(),
                request.getSparrowSmsAuthFieldName(),
                SPARROW_DEFAULT_URL,
                "token",
                activeProvider == SmsProviderEnum.SPARROW);
        upsertSms(companyId, SmsProviderEnum.AAKASH,
                request.getAakashSmsToken(),
                request.getAakashSmsFrom(),
                request.getAakashSmsApiUrl(),
                request.getAakashSmsAuthFieldName(),
                AAKASH_DEFAULT_URL,
                "auth_token",
                activeProvider == SmsProviderEnum.AAKASH);

        activateOnly(companyId, activeProvider);

        Map<SmsProviderEnum, CompanySmsConfigEntity> smsByProvider =
                indexSms(smsConfigRepository.findByCompanyId(companyId));
        return toResponse(companyId, config, email, smsByProvider);
    }

    @Override
    @Transactional(readOnly = true)
    public TestMessageResultResponse testEmail(UUID companyId, TestEmailRequest request) {
        requireCompanyId(companyId);
        CompanyEmailConfigEntity stored = emailConfigRepository.findByCompanyId(companyId).orElse(null);
        String host = firstNonBlank(request != null ? request.getSmtpHost() : null,
                stored != null ? stored.getSmtpHost() : null);
        String from = firstNonBlank(request != null ? request.getFromEmail() : null,
                stored != null ? stored.getFromEmail() : null);
        if (!StringUtils.hasText(request != null ? request.getToEmail() : null)) {
            return TestMessageResultResponse.builder().success(false).message("Recipient email is required.").build();
        }
        if (!StringUtils.hasText(host) || !StringUtils.hasText(from)) {
            return TestMessageResultResponse.builder()
                    .success(false)
                    .message("SMTP host and from email are required to test email.")
                    .build();
        }
        return TestMessageResultResponse.builder()
                .success(true)
                .message("Email settings look valid. Live SMTP send is not wired from this endpoint yet.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TestMessageResultResponse testSms(UUID companyId, TestSmsRequest request) {
        requireCompanyId(companyId);
        SmsProviderEnum provider = request != null && request.getSmsProvider() != null
                ? request.getSmsProvider()
                : smsConfigRepository.findByCompanyId(companyId).stream()
                .filter(CompanySmsConfigEntity::isActive)
                .map(CompanySmsConfigEntity::getProvider)
                .findFirst()
                .orElse(null);
        CompanySmsConfigEntity sms = provider != null
                ? smsConfigRepository.findByCompanyIdAndProvider(companyId, provider).orElse(null)
                : null;
        String token = firstNonBlank(request != null ? request.getSmsApiKey() : null,
                sms != null ? sms.getToken() : null);
        String apiUrl = firstNonBlank(request != null ? request.getSmsApiUrl() : null,
                sms != null ? sms.getApiUrl() : null);
        if (!StringUtils.hasText(request != null ? request.getToPhone() : null)) {
            return TestMessageResultResponse.builder().success(false).message("Recipient phone is required.").build();
        }
        if (provider == null || !StringUtils.hasText(token) || !StringUtils.hasText(apiUrl)) {
            return TestMessageResultResponse.builder()
                    .success(false)
                    .message("SMS provider, API URL, and token are required to test SMS.")
                    .build();
        }
        return TestMessageResultResponse.builder()
                .success(true)
                .message("SMS settings look valid. Live provider send is not wired from this endpoint yet.")
                .build();
    }

    private void activateOnly(UUID companyId, SmsProviderEnum activeProvider) {
        List<CompanySmsConfigEntity> rows = smsConfigRepository.findByCompanyId(companyId);
        for (CompanySmsConfigEntity row : rows) {
            boolean shouldBeActive = row.getProvider() == activeProvider;
            if (row.isActive() != shouldBeActive) {
                row.setActive(shouldBeActive);
                smsConfigRepository.save(row);
            }
        }
    }

    private void upsertSms(
            UUID companyId,
            SmsProviderEnum provider,
            String token,
            String from,
            String apiUrl,
            String authFieldName,
            String defaultApiUrl,
            String defaultAuthField,
            boolean active) {
        CompanySmsConfigEntity row = smsConfigRepository.findByCompanyIdAndProvider(companyId, provider)
                .orElseGet(() -> CompanySmsConfigEntity.builder()
                        .companyId(companyId)
                        .provider(provider)
                        .active(false)
                        .build());
        if (StringUtils.hasText(token)) {
            row.setToken(token.trim());
        }
        row.setSmsFrom(blankToNull(from));
        row.setApiUrl(StringUtils.hasText(apiUrl) ? apiUrl.trim() : defaultApiUrl);
        row.setAuthFieldName(StringUtils.hasText(authFieldName) ? authFieldName.trim() : defaultAuthField);
        row.setCompanyId(companyId);
        row.setProvider(provider);
        row.setActive(active);
        smsConfigRepository.save(row);
    }

    private void applyChannelFlags(CompanyMessagingConfigRequest request, CompanyConfigEntity entity) {
        if (request.getEnableEmail() != null) {
            entity.setEnableEmail(request.getEnableEmail());
        }
        if (request.getEnableSms() != null) {
            entity.setEnableSms(request.getEnableSms());
        }
        if (request.getEnableNotification() != null) {
            entity.setEnableNotification(request.getEnableNotification());
        } else if (entity.getEnableNotification() == null) {
            entity.setEnableNotification(true);
        }
    }

    private void applyEmail(CompanyMessagingConfigRequest request, CompanyEmailConfigEntity entity) {
        entity.setFromName(blankToNull(request.getFromName()));
        entity.setFromEmail(blankToNull(request.getFromEmail()));
        entity.setSmtpHost(blankToNull(request.getSmtpHost()));
        entity.setSmtpPort(request.getSmtpPort());
        entity.setSmtpUsername(blankToNull(request.getSmtpUsername()));
        if (StringUtils.hasText(request.getSmtpPassword())) {
            entity.setSmtpPassword(request.getSmtpPassword().trim());
        }
        entity.setSmtpUseTls(request.getSmtpUseTls());
    }

    private CompanyMessagingConfigResponse toResponse(
            UUID companyId,
            CompanyConfigEntity config,
            CompanyEmailConfigEntity email,
            Map<SmsProviderEnum, CompanySmsConfigEntity> smsByProvider) {
        CompanySmsConfigEntity sparrow = smsByProvider.get(SmsProviderEnum.SPARROW);
        CompanySmsConfigEntity aakash = smsByProvider.get(SmsProviderEnum.AAKASH);
        SmsProviderEnum activeProvider = resolveActiveProvider(smsByProvider);
        return CompanyMessagingConfigResponse.builder()
                .id(config != null ? config.getId() : (email != null ? email.getId() : null))
                .companyId(companyId)
                .enableEmail(config != null && Boolean.TRUE.equals(config.getEnableEmail()))
                .enableSms(config != null && Boolean.TRUE.equals(config.getEnableSms()))
                .enableNotification(config == null || !Boolean.FALSE.equals(config.getEnableNotification()))
                .fromName(email != null ? email.getFromName() : null)
                .fromEmail(email != null ? email.getFromEmail() : null)
                .smtpHost(email != null ? email.getSmtpHost() : null)
                .smtpPort(email != null ? email.getSmtpPort() : null)
                .smtpUsername(email != null ? email.getSmtpUsername() : null)
                .smtpPassword(email != null ? email.getSmtpPassword() : null)
                .smtpUseTls(email == null || email.getSmtpUseTls() == null || Boolean.TRUE.equals(email.getSmtpUseTls()))
                .smsProvider(activeProvider)
                .sparrowSmsToken(sparrow != null ? sparrow.getToken() : null)
                .sparrowSmsFrom(sparrow != null ? sparrow.getSmsFrom() : null)
                .sparrowSmsApiUrl(sparrow != null ? sparrow.getApiUrl() : SPARROW_DEFAULT_URL)
                .sparrowSmsAuthFieldName(sparrow != null ? sparrow.getAuthFieldName() : "token")
                .aakashSmsToken(aakash != null ? aakash.getToken() : null)
                .aakashSmsFrom(aakash != null ? aakash.getSmsFrom() : null)
                .aakashSmsApiUrl(aakash != null ? aakash.getApiUrl() : AAKASH_DEFAULT_URL)
                .aakashSmsAuthFieldName(aakash != null ? aakash.getAuthFieldName() : "auth_token")
                .build();
    }

    private static SmsProviderEnum resolveActiveProvider(Map<SmsProviderEnum, CompanySmsConfigEntity> smsByProvider) {
        for (Map.Entry<SmsProviderEnum, CompanySmsConfigEntity> entry : smsByProvider.entrySet()) {
            if (entry.getValue() != null && entry.getValue().isActive()) {
                return entry.getKey();
            }
        }
        return SmsProviderEnum.SPARROW;
    }

    private CompanyMessagingConfigResponse emptyResponse(UUID companyId) {
        return CompanyMessagingConfigResponse.builder()
                .companyId(companyId)
                .enableEmail(false)
                .enableSms(false)
                .enableNotification(true)
                .smtpUseTls(true)
                .smsProvider(SmsProviderEnum.SPARROW)
                .sparrowSmsApiUrl(SPARROW_DEFAULT_URL)
                .sparrowSmsAuthFieldName("token")
                .aakashSmsApiUrl(AAKASH_DEFAULT_URL)
                .aakashSmsAuthFieldName("auth_token")
                .build();
    }

    private static Map<SmsProviderEnum, CompanySmsConfigEntity> indexSms(List<CompanySmsConfigEntity> rows) {
        Map<SmsProviderEnum, CompanySmsConfigEntity> map = new EnumMap<>(SmsProviderEnum.class);
        if (rows == null) {
            return map;
        }
        for (CompanySmsConfigEntity row : rows) {
            if (row != null && row.getProvider() != null) {
                map.put(row.getProvider(), row);
            }
        }
        return map;
    }

    private static void requireCompanyId(UUID companyId) {
        if (companyId == null) {
            throw new GlobalException("COMPANY_REQUIRED", HttpStatus.BAD_REQUEST);
        }
    }

    private static String firstNonBlank(String a, String b) {
        if (StringUtils.hasText(a)) {
            return a.trim();
        }
        if (StringUtils.hasText(b)) {
            return b.trim();
        }
        return null;
    }

    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
