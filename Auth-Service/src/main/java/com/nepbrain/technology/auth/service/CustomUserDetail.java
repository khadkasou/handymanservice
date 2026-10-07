package com.nepbrain.technology.auth.service;

import com.nepbrain.technology.auth.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetail implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final UserEntity userEntity;

    /**
     * Constructs a new instance of {@link CustomUserDetail} based on the provided {@link UserEntity}.
     *
     * @param user The {@link UserEntity} from which to extract user details.
     *             <p>
     *             The constructor initializes the following fields:
     *             - {@code password}: The password of the user extracted from the provided {@link UserEntity}.
     *             - {@code username}: The username of the user extracted from the provided {@link UserEntity}.
     *             - {@code authorities}: A collection of granted authorities derived from the user's role's permissions.
     *             Each permission is transformed into a {@link SimpleGrantedAuthority} and collected into a list.
     */
    public CustomUserDetail(UserEntity user, String username, Collection<? extends GrantedAuthority> authorities) {
        this.password = user.getPassword();
        this.username = username;
        this.authorities = authorities != null ? List.copyOf(authorities) : List.of();
        this.userEntity = user;
    }

    /** @deprecated use {@link #CustomUserDetail(UserEntity, String, Collection)} */
    @Deprecated
    public CustomUserDetail(UserEntity user, String username) {
        this(user, username, RoleAuthoritySupport.authoritiesFromRoles(user.getRoles()));
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
