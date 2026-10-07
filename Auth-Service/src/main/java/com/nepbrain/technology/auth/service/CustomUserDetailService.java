package com.nepbrain.technology.auth.service;

import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.repository.RoleRepository;
import com.nepbrain.technology.auth.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Local-DB user lookup for OAuth password grant. Must stay {@code @Primary} so token
 * issuance never calls User-Service over HTTP (that deadlocks login: User→Auth→User).
 */
@Service
@Primary
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomUserDetailService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalized = normalizeUsername(username);
        UserEntity user = userRepository.findByEmailAddressIgnoreCaseOrMobileNumber(normalized, normalized);
        if (user == null) {
            throw new UsernameNotFoundException("User not found : " + username);
        }
        return new CustomUserDetail(user, normalized, authoritiesForUser(user));
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
        return roleIds.stream().filter(id -> id != null).distinct().toList();
    }

    private static String normalizeUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return username;
        }
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
