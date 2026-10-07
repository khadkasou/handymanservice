package com.nepbrain.technology.auth.service;

import com.nepbrain.technology.auth.entity.PermissionEntity;
import com.nepbrain.technology.auth.entity.RoleEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class RoleAuthoritySupport {

    private RoleAuthoritySupport() {
    }

    public static List<UUID> effectiveRoleIds(UUID roleId, List<UUID> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            return List.copyOf(roleIds);
        }
        if (roleId != null) {
            return List.of(roleId);
        }
        return List.of();
    }

    public static List<RoleEntity> orderRoles(List<UUID> roleIds, List<RoleEntity> loaded) {
        if (roleIds == null || roleIds.isEmpty() || loaded == null || loaded.isEmpty()) {
            return List.of();
        }
        var byId = loaded.stream()
                .filter(role -> role.getId() != null)
                .collect(java.util.stream.Collectors.toMap(RoleEntity::getId, role -> role, (a, b) -> a));
        return roleIds.stream().map(byId::get).filter(Objects::nonNull).toList();
    }

    public static Collection<? extends GrantedAuthority> authoritiesFromRoles(List<RoleEntity> roles) {
        Set<String> codes = new LinkedHashSet<>();
        if (roles == null) {
            return List.of();
        }
        for (RoleEntity role : roles) {
            if (role == null || role.getPermissions() == null) {
                continue;
            }
            for (PermissionEntity permission : role.getPermissions()) {
                if (permission != null && permission.getCode() != null && !permission.getCode().isBlank()) {
                    codes.add(permission.getCode());
                }
            }
        }
        List<GrantedAuthority> authorities = new ArrayList<>(codes.size());
        for (String code : codes) {
            authorities.add(new SimpleGrantedAuthority(code));
        }
        return authorities;
    }
}
