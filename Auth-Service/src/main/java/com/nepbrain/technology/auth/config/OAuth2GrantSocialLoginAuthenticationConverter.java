package com.nepbrain.technology.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nepbrain.technology.auth.config.AuthorizationGrantTypeSocialLogin.GRANT_SOCIAL_LOGIN;

public class OAuth2GrantSocialLoginAuthenticationConverter implements AuthenticationConverter {

    private static final String USERNAME_PARAMETER = "username";

    private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach(
                (key, values) -> {
                    for (String value : values) {
                        parameters.add(key, value);
                    }
                });
        return parameters;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!GRANT_SOCIAL_LOGIN.getValue().equals(grantType)) {
            return null;
        }
        MultiValueMap<String, String> parameters = getParameters(request);

        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        String username = parameters.getFirst(USERNAME_PARAMETER);
        if (!StringUtils.hasText(username) || parameters.get(USERNAME_PARAMETER).size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes =
                    new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }

        Map<String, Object> additionalParameters =
                parameters.entrySet().stream()
                        .filter(
                                entry ->
                                        !OAuth2ParameterNames.GRANT_TYPE.equals(entry.getKey())
                                                && !OAuth2ParameterNames.SCOPE.equals(entry.getKey())
                                                && !USERNAME_PARAMETER.equals(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFirst()));

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        return new GrantSocialLoginAuthenticationToken(
                clientPrincipal, username, requestedScopes, additionalParameters);
    }
}
