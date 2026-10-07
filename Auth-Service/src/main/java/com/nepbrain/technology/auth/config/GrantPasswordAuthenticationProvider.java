package com.nepbrain.technology.auth.config;

import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.ErrorCodeEnum;
import com.nepbrain.technology.auth.enums.UserStatusEnum;
import com.nepbrain.technology.auth.exception.ServiceException;
import com.nepbrain.technology.auth.repository.RoleRepository;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.auth.service.CustomUserDetail;
import com.nepbrain.technology.auth.payload.response.ServiceResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import static com.nepbrain.technology.auth.config.AuthorizationGrantTypePassword.GRANT_PASSWORD;

public class GrantPasswordAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI =
            "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    private final OAuth2AuthorizationService authorizationService;
    private final UserDetailsService userDetailsService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public GrantPasswordAuthenticationProvider(
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RoleRepository roleRepository,
            HttpServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
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

    /**
     * Authenticates a user using the custom password grant type.
     *
     * @param authentication The authentication object containing the username and password.
     * @return An authenticated {@link Authentication} object containing the user's details and access
     * token.
     * @throws AuthenticationException If the authentication fails due to invalid client, unauthorized
     *                                 client, or access denied.
     */
    @Override

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        GrantPasswordAuthenticationToken customPasswordAuthenticationToken =
                (GrantPasswordAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal =
                getAuthenticatedClientElseThrowInvalidClient(customPasswordAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        if (registeredClient == null
                || !registeredClient.getAuthorizationGrantTypes().contains(GRANT_PASSWORD)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        String username = customPasswordAuthenticationToken.getUsername();
        if (username != null) {
            username = username.trim().toLowerCase();
        }
        String password = customPasswordAuthenticationToken.getPassword();
        UserEntity userEntity = userRepository.findByEmailAddressIgnoreCase(username);
        if (userEntity == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
        }
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
        }
        UserDetails user = new CustomUserDetail(userEntity, username, authoritiesForUser(userEntity));

        Authentication usernamePasswordAuthenticationToken = getAuthentication(userEntity, user);
        DefaultOAuth2TokenContext.Builder tokenContextBuilder =
                DefaultOAuth2TokenContext.builder()
                        .registeredClient(registeredClient)
                        .principal(usernamePasswordAuthenticationToken)
                        .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                        .principal(usernamePasswordAuthenticationToken)
                        .authorizedScopes(registeredClient.getScopes())
                        .authorizationGrantType(GRANT_PASSWORD)
                        .authorizationGrant(customPasswordAuthenticationToken);

        // Generate the access token
        OAuth2TokenContext tokenContext =
                tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();

        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);

        if (generatedAccessToken == null) {
            OAuth2Error error =
                    new OAuth2Error(
                            OAuth2ErrorCodes.SERVER_ERROR,
                            "The token generator failed to generate the access token.",
                            ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }

        // ----- Access token -----
        OAuth2AccessToken accessToken =
                new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        generatedAccessToken.getTokenValue(),
                        generatedAccessToken.getIssuedAt(),
                        generatedAccessToken.getExpiresAt(),
                        tokenContext.getAuthorizedScopes());


        // Initialize the OAuth2Authorization
        OAuth2Authorization.Builder authorizationBuilder =
                OAuth2Authorization.withRegisteredClient(registeredClient)
                        .attribute(Principal.class.getName(), usernamePasswordAuthenticationToken)
                        .principalName(String.valueOf(userEntity.getId()))
                        .authorizationGrantType(GRANT_PASSWORD)
                        .authorizedScopes(registeredClient.getScopes());

        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(
                    accessToken,
                    (metadata) ->
                            metadata.put(
                                    OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                                    ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;

        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)
                && !clientPrincipal
                .getClientAuthenticationMethod()
                .equals(ClientAuthenticationMethod.NONE)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();

            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);

            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                OAuth2Error error =
                        new OAuth2Error(
                                OAuth2ErrorCodes.SERVER_ERROR,
                                "The token generator failed to generate the refresh token.",
                                ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }

            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(refreshToken);
        }
        OAuth2Authorization authorization = authorizationBuilder
                .build();
        this.authorizationService.save(authorization);
        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient, usernamePasswordAuthenticationToken, accessToken, refreshToken);
    }

    private Collection<? extends GrantedAuthority> authoritiesForUser(UserEntity userEntity) {
        List<UUID> roleIdList = resolveRoleIds(userEntity);
        if (roleIdList.isEmpty()) {
            return List.of();
        }
        // Fast path: distinct permission codes (avoids EntityGraph cartesian product on multi-role users).
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
        return roleIds.stream().filter(id -> id != null).distinct().toList();
    }

    private static Authentication getAuthentication(UserEntity dbUser, UserDetails user) throws ServiceException {
        User user1 =
                new User(
                        String.valueOf(dbUser.getId()), dbUser.getPassword(), dbUser.isEnabled(), dbUser.isAccountNonExpired(), dbUser.isCredentialsNonExpired(), dbUser.isAccountNonLocked(), user.getAuthorities());
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
        return new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return GrantPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
