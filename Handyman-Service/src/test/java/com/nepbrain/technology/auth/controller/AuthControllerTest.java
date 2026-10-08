package com.nepbrain.technology.auth.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.auth.payload.request.*;
import com.nepbrain.technology.auth.payload.response.LoginResponse;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.auth.service.AuthService;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthService authService;

    @InjectMocks private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_returnsCreatedWithGlobalResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        LoginResponse loginResponse = LoginResponse.builder()
                .status(true)
                .message("Registration successful")
                .userData(UserResponse.builder().id(userId).email("user@example.com").build())
                .build();
        when(authService.register(any(RegisterRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(loginResponse).build());

        RegisterRequest request = RegisterRequest.builder()
                .email("user@example.com")
                .password("secret")
                .firstName("John")
                .build();

        mockMvc.perform(post("/api/v2/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.userData.email").value("user@example.com"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_returnsOk() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(LoginResponse.builder()
                .status(true)
                .message("Login successful")
                .userData(UserResponse.builder().email("user@example.com").build())
                .build()).build());

        mockMvc.perform(post("/api/v2/auth/login").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(LoginRequest.builder()
                .email("user@example.com")
                .password("secret")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Login successful"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void socialLogin_returnsOk() throws Exception {
        when(authService.socialLogin(any(LoginRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(LoginResponse.builder()
                .status(true)
                .message("Social login successful")
                .build()).build());

        mockMvc.perform(post("/api/v2/auth/social-login").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(LoginRequest.builder()
                .email("social@example.com")
                .loginType("google")
                .build())))
                .andExpect(status().isOk());

        verify(authService).socialLogin(any(LoginRequest.class));
    }

    @Test
    void updateProfile_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(authService.updateProfile(any(UpdateProfileRequest.class)))
                .thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(UserResponse.builder().id(id).firstName("New").build()).build());

        mockMvc.perform(post("/api/v2/auth/update-profile").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(UpdateProfileRequest.builder()
                .id(id)
                .firstName("New")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("New"));

        verify(authService).updateProfile(any(UpdateProfileRequest.class));
    }

    @Test
    void changePassword_returnsSuccessMessage() throws Exception {
        when(authService.changePassword(any(ChangePasswordRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Password changed successfully").build());

        mockMvc.perform(post("/api/v2/auth/change-password").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(ChangePasswordRequest.builder()
                .oldPassword("old")
                .newPassword("new")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        verify(authService).changePassword(any(ChangePasswordRequest.class));
    }

    @Test
    void forgotPassword_returnsSuccessMessage() throws Exception {
        when(authService.forgotPassword(any(ForgotPasswordRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Password reset email sent").build());

        mockMvc.perform(post("/api/v2/auth/forgot-password").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(ForgotPasswordRequest.builder()
                .email("user@example.com")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset email sent"));

        verify(authService).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    void deleteUserAccount_usesQueryParam() throws Exception {
        UUID id = UUID.randomUUID();
        when(authService.deleteUserAccount(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Account deleted").build());

        mockMvc.perform(post("/api/v2/auth/delete-user-account").param("id", id.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted"));

        verify(authService).deleteUserAccount(eq(id));
    }

    @Test
    void deleteUserAccount_usesHeaderWhenParamMissing() throws Exception {
        UUID id = UUID.randomUUID();
        when(authService.deleteUserAccount(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).build());

        mockMvc.perform(post("/api/v2/auth/delete-user-account").header("id", id.toString())).andExpect(status().isOk());

        verify(authService).deleteUserAccount(eq(id));
    }

    @Test
    void verifyEmail_returnsOk() throws Exception {
        when(authService.verifyEmail(any(EmailVerifyRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Email verified").build());

        mockMvc.perform(post("/api/v2/auth/user-email-verify").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(EmailVerifyRequest.builder()
                .email("user@example.com")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified"));

        verify(authService).verifyEmail(any(EmailVerifyRequest.class));
    }

    @Test
    void logout_withId_callsService() throws Exception {
        UUID id = UUID.randomUUID();
        when(authService.logout(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Logged out").build());

        mockMvc.perform(get("/api/v2/auth/logout").param("id", id.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out"));

        verify(authService).logout(eq(id));
    }

    @Test
    void logout_withoutId_skipsServiceCall() throws Exception {
        mockMvc.perform(get("/api/v2/auth/logout")).andExpect(status().isOk());

        verify(authService, never()).logout(any());
    }

    @Test
    void userDetail_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(authService.getUserDetail(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(UserResponse.builder().id(id).email("user@example.com").build()).build());

        mockMvc.perform(get("/api/v2/auth/user-detail").param("id", id.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@example.com"));

        verify(authService).getUserDetail(eq(id));
    }
}
