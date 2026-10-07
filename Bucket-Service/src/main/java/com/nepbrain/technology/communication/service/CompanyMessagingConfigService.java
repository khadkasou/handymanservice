package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.CompanyMessagingConfigRequest;
import com.nepbrain.technology.communication.payload.request.TestEmailRequest;
import com.nepbrain.technology.communication.payload.request.TestSmsRequest;
import com.nepbrain.technology.communication.payload.response.CompanyMessagingConfigResponse;
import com.nepbrain.technology.communication.payload.response.TestMessageResultResponse;

import java.util.UUID;

public interface CompanyMessagingConfigService {

    CompanyMessagingConfigResponse getByCompanyId(UUID companyId);

    CompanyMessagingConfigResponse save(UUID companyId, CompanyMessagingConfigRequest request);

    TestMessageResultResponse testEmail(UUID companyId, TestEmailRequest request);

    TestMessageResultResponse testSms(UUID companyId, TestSmsRequest request);
}
