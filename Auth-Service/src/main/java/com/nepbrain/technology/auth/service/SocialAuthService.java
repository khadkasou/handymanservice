package com.nepbrain.technology.auth.service;

import com.nepbrain.technology.auth.entity.RoleEntity;
import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.UserStatusEnum;
import com.nepbrain.technology.auth.repository.RoleRepository;
import com.nepbrain.technology.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Processes OAuth2 user information and finds or creates a user.
     *
     * @param oauth2User The OAuth2 user information from the provider
     * @param provider   The provider name (e.g., "google", "facebook")
     * @return UserDetails for authentication
     */
    @Transactional
    public UserDetails processOAuth2User(OAuth2User oauth2User, String provider) {
        String providerId = oauth2User.getName();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        // For Facebook, email might be in a different attribute
        if (email == null && "facebook".equalsIgnoreCase(provider)) {
            email = oauth2User.getAttribute("id") + "@facebook.com";
        }

        // UserEntity has authProvider only (no providerId) — resolve by email.
        UserEntity user = email != null
                ? userRepository.findByEmailAddressIgnoreCase(email.trim().toLowerCase())
                : null;

        if (user == null) {
            user = createNewUser(provider, providerId, email, name);
        } else if (user.getAuthProvider() == null || user.getAuthProvider().isBlank()) {
            user.setAuthProvider(provider);
            userRepository.save(user);
        }

        return new User(
                String.valueOf(user.getId()),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                Collections.emptyList() // Authorities will be loaded by CustomUserDetailService
        );
    }

    private UserEntity createNewUser(String provider, String providerId, String email, String name) {
        // Get default role - you may need to adjust this based on your requirements
        RoleEntity defaultRole = roleRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No roles found in database. Please create at least one role."));

        // Generate a random password for social login users
        String randomPassword = UUID.randomUUID().toString();

        UserEntity user = UserEntity.builder()
                .emailAddress(email != null ? email.trim().toLowerCase() : providerId + "@" + provider + ".com")
                .password(passwordEncoder.encode(randomPassword))
                .authProvider(provider)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .status(UserStatusEnum.ACTIVE)
                .roles(List.of(defaultRole))
                .build();

        return userRepository.save(user);
    }
}

