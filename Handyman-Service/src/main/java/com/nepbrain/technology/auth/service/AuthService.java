package com.nepbrain.technology.auth.service;
import com.nepbrain.technology.auth.payload.request.*;
import com.nepbrain.technology.auth.payload.response.LoginResponse;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import java.util.UUID;
public interface AuthService {
    LoginResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse socialLogin(LoginRequest request);
    UserResponse updateProfile(UpdateProfileRequest request);
    void changePassword(ChangePasswordRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void deleteUserAccount(UUID userId);
    void verifyEmail(EmailVerifyRequest request);
    void logout(UUID userId);
    UserResponse getUserDetail(UUID id);
}
