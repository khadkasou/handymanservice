package com.nepbrain.technology.auth.service.impl;
import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.auth.payload.request.*;
import com.nepbrain.technology.auth.payload.response.LoginResponse;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.auth.service.AuthService;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper; import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static com.nepbrain.technology.auth.enums.ErrorCodeEnum.*;

@Slf4j @Service @RequiredArgsConstructor @Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) throw new GlobalException(AUTH001.name());
        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .username(request.getUsername() != null ? request.getUsername() : request.getEmail())
                .passwordHash(request.getPassword())
                .firstName(request.getFirstName()).lastName(request.getLastName())
                .contactNumber(request.getContactNumber())
                .userType(parseUserType(request.getUserType()))
                .status(StatusEnum.ACTIVE)
                .countryId(request.getCountryId()).stateId(request.getStateId()).cityId(request.getCityId())
                .address(request.getAddress())
                .loginType(request.getLoginType() != null ? request.getLoginType() : "user")
                .uid(request.getUid()).emailVerified(false)
                .apiToken(UUID.randomUUID().toString()).build();
        return toLogin(userRepository.save(user), "Registration successful");
    }
    @Override public LoginResponse login(LoginRequest request) {
        UserEntity user = resolve(request.getEmail(), request.getUsername());
        if (user.getPasswordHash() == null || !user.getPasswordHash().equals(request.getPassword()))
            throw new GlobalException(AUTH003.name());
        user.setApiToken(UUID.randomUUID().toString());
        return toLogin(userRepository.save(user), "Login successful");
    }
    @Override public LoginResponse socialLogin(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            user = UserEntity.builder().email(request.getEmail()).username(request.getEmail())
                    .passwordHash(UUID.randomUUID().toString())
                    .firstName(request.getFirstName()).lastName(request.getLastName())
                    .contactNumber(request.getContactNumber()).profileImage(request.getProfileImage())
                    .userType(UserTypeEnum.USER).status(StatusEnum.ACTIVE)
                    .loginType(request.getLoginType()).uid(request.getUid())
                    .emailVerified(true).apiToken(UUID.randomUUID().toString()).build();
        } else {
            user.setApiToken(UUID.randomUUID().toString());
            if (request.getUid() != null) user.setUid(request.getUid());
        }
        return toLogin(userRepository.save(user), "Social login successful");
    }
    @Override public UserResponse updateProfile(UpdateProfileRequest request) {
        UserEntity user = userRepository.findById(request.getId()).orElseThrow(() -> new GlobalException(AUTH002.name()));
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getContactNumber() != null) user.setContactNumber(request.getContactNumber());
        if (request.getCountryId() != null) user.setCountryId(request.getCountryId());
        if (request.getStateId() != null) user.setStateId(request.getStateId());
        if (request.getCityId() != null) user.setCityId(request.getCityId());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());
        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        return toUser(userRepository.save(user));
    }
    @Override public void changePassword(ChangePasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().isBlank())
            throw new GlobalException(AUTH004.name());
        log.info("Change password stub");
    }
    @Override public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new GlobalException(AUTH002.name()));
    }
    @Override public void deleteUserAccount(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(AUTH002.name()));
        user.setStatus(StatusEnum.DELETED); userRepository.save(user);
    }
    @Override public void verifyEmail(EmailVerifyRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new GlobalException(AUTH002.name()));
        user.setEmailVerified(true); userRepository.save(user);
    }
    @Override public void logout(UUID userId) {
        userRepository.findById(userId).ifPresent(u -> { u.setApiToken(null); userRepository.save(u); });
    }
    @Override @Transactional(readOnly = true)
    public UserResponse getUserDetail(UUID id) {
        return toUser(userRepository.findById(id).orElseThrow(() -> new GlobalException(AUTH002.name())));
    }
    private UserEntity resolve(String email, String username) {
        if (email != null) return userRepository.findByEmail(email).orElseThrow(() -> new GlobalException(AUTH002.name()));
        return userRepository.findByUsername(username).orElseThrow(() -> new GlobalException(AUTH002.name()));
    }
    private UserTypeEnum parseUserType(String type) {
        if (type == null) return UserTypeEnum.USER;
        return UserTypeEnum.valueOf(type.trim().toUpperCase());
    }
    private LoginResponse toLogin(UserEntity user, String msg) {
        return LoginResponse.builder().status(true).message(msg).userData(toUser(user)).build();
    }
    private UserResponse toUser(UserEntity user) {
        UserResponse r = modelMapper.map(user, UserResponse.class);
        r.setUserType(user.getUserType() != null ? user.getUserType().name().toLowerCase() : null);
        r.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        return r;
    }
}
