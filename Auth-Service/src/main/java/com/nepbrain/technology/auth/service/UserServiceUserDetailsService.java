package com.nepbrain.technology.auth.service;

import com.nepbrain.technology.auth.communication.UserServiceCommunication;
import com.nepbrain.technology.auth.entity.RoleEntity;
import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.UserStatusEnum;
import com.nepbrain.technology.auth.payload.response.UserServiceResponse;
import com.nepbrain.technology.auth.repository.RoleRepository;
import com.nepbrain.technology.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Optional User-Service HTTP lookup. Disabled by default so password grant uses
 * {@link CustomUserDetailService} only (avoids User↔Auth deadlock + dual UDS warning).
 * Enable with {@code auth.user-service-details.enabled=true} if needed.
 */
@Service
@ConditionalOnProperty(name = "auth.user-service-details.enabled", havingValue = "true")
@RequiredArgsConstructor
public class UserServiceUserDetailsService implements UserDetailsService {

    private final UserServiceCommunication userServiceCommunication;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            String normalized = username != null ? username.trim().toLowerCase() : null;
            UserServiceResponse userResponse = userServiceCommunication.getUserByEmail(normalized);
            UserEntity localUser = userRepository.findByEmailAddressIgnoreCaseOrMobileNumber(normalized, normalized);
            UserEntity userEntity = convertToUserEntity(userResponse);
            if (localUser != null && localUser.getPassword() != null && !localUser.getPassword().isEmpty()) {
                userEntity.setPassword(localUser.getPassword());
            }

            List<UUID> roleIdList = RoleAuthoritySupport.effectiveRoleIds(
                    userResponse.getRoleId(),
                    userResponse.getRoleIds());
            return new CustomUserDetail(userEntity, normalized, authoritiesFromRoleIds(roleIdList));
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found: " + username, e);
        }
    }

    private Collection<? extends GrantedAuthority> authoritiesFromRoleIds(List<UUID> roleIdList) {
        if (roleIdList == null || roleIdList.isEmpty()) {
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

    private UserEntity convertToUserEntity(UserServiceResponse userResponse) {
        List<UUID> roleIdList = RoleAuthoritySupport.effectiveRoleIds(
                userResponse.getRoleId(),
                userResponse.getRoleIds()
        );
        List<RoleEntity> roles = roleIdList.isEmpty()
                ? List.of()
                : RoleAuthoritySupport.orderRoles(roleIdList, roleRepository.findAllById(roleIdList));

        UserStatusEnum status = UserStatusEnum.ACTIVE;
        if (userResponse.getStatus() != null) {
            try {
                status = UserStatusEnum.valueOf(userResponse.getStatus());
            } catch (IllegalArgumentException _) {
            }
        }

        return UserEntity.builder()
                .emailAddress(userResponse.getEmailAddress())
                .mobileNumber(userResponse.getMobileNumber())
                .password("")
                .accountNonExpired(userResponse.getAccountNonExpired() != null ? userResponse.getAccountNonExpired() : true)
                .accountNonLocked(userResponse.getAccountNonLocked() != null ? userResponse.getAccountNonLocked() : true)
                .credentialsNonExpired(userResponse.getCredentialsNonExpired() != null ? userResponse.getCredentialsNonExpired() : true)
                .enabled(userResponse.getEnabled() != null ? userResponse.getEnabled() : true)
                .status(status)
                .roles(roles)
                .build();
    }
}
