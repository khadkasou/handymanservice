package com.nepbrain.technology.auth.service.impl;

import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.ErrorCodeEnum;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.auth.payload.request.ChangePasswordRequest;
import com.nepbrain.technology.auth.payload.request.EmailVerifyRequest;
import com.nepbrain.technology.auth.payload.request.ForgotPasswordRequest;
import com.nepbrain.technology.auth.payload.request.LoginRequest;
import com.nepbrain.technology.auth.payload.request.RegisterRequest;
import com.nepbrain.technology.auth.payload.request.UpdateProfileRequest;
import com.nepbrain.technology.auth.payload.response.LoginResponse;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.auth.service.AuthService;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public GlobalResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new GlobalException(ErrorCodeEnum.AUTH001.name(), HttpStatus.CONFLICT);
        }

        String username = request.getUsername() != null ? request.getUsername() : request.getEmail();
        String loginType = request.getLoginType() != null ? request.getLoginType() : "user";

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setUsername(username);
        user.setPasswordHash(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setContactNumber(request.getContactNumber());
        user.setUserType(parseUserType(request.getUserType()));
        user.setStatus(StatusEnum.ACTIVE);
        user.setCountryId(request.getCountryId());
        user.setStateId(request.getStateId());
        user.setCityId(request.getCityId());
        user.setAddress(request.getAddress());
        user.setLoginType(loginType);
        user.setUid(request.getUid());
        user.setEmailVerified(false);
        user.setApiToken(UUID.randomUUID().toString());
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.auth.register.success", loginResponse(userRepository.save(user), "Registration successful"));
    }

    @Override
    public GlobalResponse login(LoginRequest request) {
        UserEntity user = findUser(request.getEmail(), request.getUsername());
        if (user.getPasswordHash() == null || !user.getPasswordHash().equals(request.getPassword())) {
            throw new GlobalException(ErrorCodeEnum.AUTH003.name());
        }
        user.setApiToken(UUID.randomUUID().toString());
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.auth.login.success", loginResponse(userRepository.save(user), "Login successful"));
    }

    @Override
    public GlobalResponse socialLogin(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setUsername(request.getEmail());
            user.setPasswordHash(UUID.randomUUID().toString());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setContactNumber(request.getContactNumber());
            user.setProfileImage(request.getProfileImage());
            user.setUserType(UserTypeEnum.USER);
            user.setStatus(StatusEnum.ACTIVE);
            user.setLoginType(request.getLoginType());
            user.setUid(request.getUid());
            user.setEmailVerified(true);
            user.setApiToken(UUID.randomUUID().toString());
        } else {
            user.setApiToken(UUID.randomUUID().toString());
            if (request.getUid() != null) {
                user.setUid(request.getUid());
            }
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.auth.social.login.success", loginResponse(userRepository.save(user), "Social login successful"));
    }

    @Override
    public GlobalResponse updateProfile(UpdateProfileRequest request) {
        UserEntity user = userRepository.findById(request.getId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.AUTH002.name()));
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getContactNumber() != null) {
            user.setContactNumber(request.getContactNumber());
        }
        if (request.getCountryId() != null) {
            user.setCountryId(request.getCountryId());
        }
        if (request.getStateId() != null) {
            user.setStateId(request.getStateId());
        }
        if (request.getCityId() != null) {
            user.setCityId(request.getCityId());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.auth.profile.updated.success", toUser(userRepository.save(user)));
    }

    @Override
    public GlobalResponse changePassword(ChangePasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new GlobalException(ErrorCodeEnum.AUTH004.name());
        }
        log.info("Change password requested");
        return GlobalResponseBuilder.buildSuccessResponse("message.auth.password.changed.success");
    }

    @Override
    public GlobalResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.AUTH002.name()));
        return GlobalResponseBuilder.buildSuccessResponse("message.auth.password.reset.success");
    }

    @Override
    public GlobalResponse deleteUserAccount(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.AUTH002.name()));
        user.setStatus(StatusEnum.DELETED);
        userRepository.save(user);
        return GlobalResponseBuilder.buildSuccessResponse("message.auth.account.status.success");
    }

    @Override
    public GlobalResponse verifyEmail(EmailVerifyRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.AUTH002.name()));
        user.setEmailVerified(true);
        userRepository.save(user);
        return GlobalResponseBuilder.buildSuccessResponse("message.auth.email.verified.success");
    }

    @Override
    public GlobalResponse logout(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return GlobalResponseBuilder.buildSuccessResponse("message.auth.logout.success");
        }
        user.setApiToken(null);
        userRepository.save(user);
        return GlobalResponseBuilder.buildSuccessResponse("message.auth.logout.success");
    }

    @Override
    public GlobalResponse getUserDetail(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.AUTH002.name()));
        return GlobalResponseBuilder.buildSuccessResponse(toUser(user));
    }

    private UserEntity findUser(String email, String username) {
        if (email != null) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.AUTH002.name()));
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.AUTH002.name()));
    }

    private UserTypeEnum parseUserType(String type) {
        if (type == null) {
            return UserTypeEnum.USER;
        }
        return UserTypeEnum.valueOf(type.trim().toUpperCase());
    }

    private LoginResponse loginResponse(UserEntity user, String message) {
        return LoginResponse.builder()
                .status(true)
                .message(message)
                .userData(toUser(user))
                .build();
    }

    private UserResponse toUser(UserEntity user) {
        String userType = user.getUserType() == null ? null : user.getUserType().name().toLowerCase();
        String status = user.getStatus() == null ? null : user.getStatus().name();
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .contactNumber(user.getContactNumber())
                .userType(userType)
                .status(status)
                .profileImage(user.getProfileImage())
                .countryId(user.getCountryId())
                .stateId(user.getStateId())
                .cityId(user.getCityId())
                .address(user.getAddress())
                .uid(user.getUid())
                .loginType(user.getLoginType())
                .emailVerified(user.getEmailVerified())
                .apiToken(user.getApiToken())
                .displayName(user.getDisplayName())
                .designation(user.getDesignation())
                .isFeatured(user.getIsFeatured())
                .providerId(user.getProviderId())
                .build();
    }
}
