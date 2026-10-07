package com.nepbrain.technology.auth.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthTokenSettingsRequest {

    /** Access token time-to-live in seconds (applied to newly issued tokens). */
    private Long accessTokenTtlSeconds;

    /** Refresh token time-to-live in seconds (applied to newly issued tokens). */
    private Long refreshTokenTtlSeconds;
}
