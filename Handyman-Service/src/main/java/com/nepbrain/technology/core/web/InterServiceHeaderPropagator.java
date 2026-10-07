package com.nepbrain.technology.core.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

@Component
public class InterServiceHeaderPropagator {

    private static final String[][] HEADER_GROUPS = {
            {"X-Company-Id", "companyId", "company_id", "company-id"},
            {"X-Tenant-Id", "tenantId", "tenant_id", "tenant-id"},
            {"X-Branch-Id", "branchId", "branch_id", "branch-id"},
            {"X-Forwarded-Host", "x-forwarded-host"},
            {"Host", "host"}
    };

    public Consumer<WebClient.RequestHeadersSpec<?>> tenantScopeHeaders() {
        return spec -> spec.headers(h -> applyTo(h));
    }

    public void applyTo(org.springframework.http.HttpHeaders headers) {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return;
        }
        for (String[] group : HEADER_GROUPS) {
            copyFirstPresent(request, headers, group);
        }
    }

    private static void copyFirstPresent(
            HttpServletRequest request, org.springframework.http.HttpHeaders target, String[] names) {
        for (String name : names) {
            String value = request.getHeader(name);
            if (!StringUtils.hasText(value)) {
                value = request.getHeader(name.toLowerCase());
            }
            if (StringUtils.hasText(value)) {
                for (String alias : names) {
                    target.set(alias, value.trim());
                }
                return;
            }
        }
    }

    private static HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}
