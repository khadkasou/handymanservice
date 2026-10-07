package com.nepbrain.technology.auth.config;

import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.ErrorCodeEnum;
import com.nepbrain.technology.auth.enums.UserStatusEnum;
import com.nepbrain.technology.auth.exception.ServiceException;
import com.nepbrain.technology.auth.payload.response.ServiceResponseBuilder;
import com.nepbrain.technology.auth.repository.RoleRepository;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.auth.service.CustomUserDetail;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.nepbrain.technology.auth.config.AuthorizationGrantTypeSocialLogin.GRANT_SOCIAL_LOGIN;

/**
 * Issues opaque access/refresh tokens for users authenticated via social providers.
 * Requires an authenticated confidential OAuth client; does not validate a user password.
 */
public class GrantSocialLoginAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI =
            "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public GrantSocialLoginAuthenticationProvider(
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
            UserRepository userRepository,
            RoleRepository roleRepository) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        Assert.notNull(userRepository, "userRepository cannot be null");
        Assert.notNull(roleRepository, "roleRepository cannot be null");
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(
            Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(
                Objects.requireNonNull(authentication.getPrincipal()).getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        GrantSocialLoginAuthenticationToken socialToken =
                (GrantSocialLoginAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal =
                getAuthenticatedClientElseThrowInvalidClient(socialToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        if (registeredClient == null
                || !registeredClient.getAuthorizationGrantTypes().contains(GRANT_SOCIAL_LOGIN)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        String username = socialToken.getUsername();
        if (username != null) {
            username = username.trim().toLowerCase();
        }
        UserEntity userEntity = userRepository.findByEmailAddressIgnoreCase(username);
        if (userEntity == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
        }
        if (!userEntity.isEnabled() || !userEntity.isAccountNonLocked()) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
        }

        UserDetails user = new CustomUserDetail(userEntity, username, authoritiesForUser(userEntity));
        Authentication userAuthentication = getAuthentication(userEntity, user);

        DefaultOAuth2TokenContext.Builder tokenContextBuilder =
                DefaultOAuth2TokenContext.builder()
                        .registeredClient(registeredClient)
                        .principal(userAuthentication)
                        .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                        .authorizedScopes(registeredClient.getScopes())
                        .authorizationGrantType(GRANT_SOCIAL_LOGIN)
                        .authorizationGrant(socialToken);

        OAuth2TokenContext tokenContext =
                tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodes.SERVER_ERROR,
                            "The token generator failed to generate the access token.",
                            ERROR_URI));
        }

        OAuth2AccessToken accessToken =
                new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        generatedAccessToken.getTokenValue(),
                        generatedAccessToken.getIssuedAt(),
                        generatedAccessToken.getExpiresAt(),
                        tokenContext.getAuthorizedScopes());

        OAuth2Authorization.Builder authorizationBuilder =
                OAuth2Authorization.withRegisteredClient(registeredClient)
                        .attribute(Principal.class.getName(), userAuthentication)
                        .principalName(String.valueOf(userEntity.getId()))
                        .authorizationGrantType(GRANT_SOCIAL_LOGIN)
                        .authorizedScopes(registeredClient.getScopes());

        if (generatedAccessToken instanceof ClaimAccessor claimAccessor) {
            authorizationBuilder.token(
                    accessToken,
                    (metadata) ->
                            metadata.put(
                                    OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                                    claimAccessor.getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)
                && !clientPrincipal
                        .getClientAuthenticationMethod()
                        .equals(ClientAuthenticationMethod.NONE)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error(
                                OAuth2ErrorCodes.SERVER_ERROR,
                                "The token generator failed to generate the refresh token.",
                                ERROR_URI));
            }
            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(refreshToken);
        }

        this.authorizationService.save(authorizationBuilder.build());
        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient, userAuthentication, accessToken, refreshToken);
    }

    private Collection<? extends GrantedAuthority> authoritiesForUser(UserEntity userEntity) {
        List<UUID> roleIdList = resolveRoleIds(userEntity);
        if (roleIdList.isEmpty()) {
            return List.of();
        }
        List<String> codes = roleRepository.findPermissionCodesByRoleIds(roleIdList);
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return codes.stream()
                .filter(code -> code != null && !code.isBlank())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    private List<UUID> resolveRoleIds(UserEntity userEntity) {
        if (userEntity == null || userEntity.getId() == null) {
            return List.of();
        }
        List<UUID> roleIds = userRepository.findRoleIdsByUserId(userEntity.getId());
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return roleIds.stream().filter(Objects::nonNull).distinct().toList();
    }

    private static Authentication getAuthentication(UserEntity dbUser, UserDetails user)
            throws ServiceException {
        User springUser =
                new User(
                        String.valueOf(dbUser.getId()),
                        dbUser.getPassword(),
                        dbUser.isEnabled(),
                        dbUser.isAccountNonExpired(),
                        dbUser.isCredentialsNonExpired(),
                        dbUser.isAccountNonLocked(),
                        user.getAuthorities());
        if (!dbUser.isEnabled()) {
            ServiceResponseBuilder.sendError(HttpStatus.FORBIDDEN, ErrorCodeEnum.IAM001.getCode());
        }
        if (!dbUser.isAccountNonExpired()) {
            ServiceResponseBuilder.sendError(HttpStatus.FORBIDDEN, ErrorCodeEnum.IAM04.getCode());
        }
        if (!dbUser.isCredentialsNonExpired()) {
            ServiceResponseBuilder.sendError(HttpStatus.FORBIDDEN, ErrorCodeEnum.IAM05.getCode());
        }
        if (!dbUser.isAccountNonLocked() && dbUser.getStatus().equals(UserStatusEnum.LOCKED)) {
            ServiceResponseBuilder.sendError(HttpStatus.FORBIDDEN, ErrorCodeEnum.IAM06.getCode());
        }
        return new UsernamePasswordAuthenticationToken(springUser, null, springUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return GrantSocialLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
