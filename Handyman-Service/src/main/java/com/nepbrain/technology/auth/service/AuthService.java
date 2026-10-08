package com.nepbrain.technology.auth.service;

import com.nepbrain.technology.auth.payload.request.*;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface AuthService {

    GlobalResponse register(RegisterRequest request);

    GlobalResponse login(LoginRequest request);

    GlobalResponse socialLogin(LoginRequest request);

    GlobalResponse updateProfile(UpdateProfileRequest request);

    GlobalResponse changePassword(ChangePasswordRequest request);

    GlobalResponse forgotPassword(ForgotPasswordRequest request);

    GlobalResponse deleteUserAccount(UUID userId);

    GlobalResponse verifyEmail(EmailVerifyRequest request);

    GlobalResponse logout(UUID userId);

    GlobalResponse getUserDetail(UUID id);
}
