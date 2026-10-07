package com.nepbrain.technology.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthTokenSettingsResponse {

    private String clientId;
    private Long accessTokenTtlSeconds;
    private Long refreshTokenTtlSeconds;
    private String accessTokenTtlLabel;
    private String refreshTokenTtlLabel;
}
