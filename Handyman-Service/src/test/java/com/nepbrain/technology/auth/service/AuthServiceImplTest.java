package com.nepbrain.technology.auth.service;

import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.auth.payload.request.*;
import com.nepbrain.technology.auth.payload.response.LoginResponse;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.auth.service.impl.AuthServiceImpl;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;

    @Spy private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks private AuthServiceImpl authService;

    @BeforeEach
    void stubSave() {
        lenient().when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
                UserEntity entity = invocation.getArgument(0);
                if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
        }
                return entity;
        });
    }

    @Test
    void register_success() {
        RegisterRequest request = RegisterRequest.builder()
                .email("user@example.com")
                .username("handyman")
                .password("secret")
                .firstName("John")
                .lastName("Doe")
                .contactNumber("9800000000")
                .userType("USER")
                .build();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        LoginResponse response = (LoginResponse) authService.register(request).getData();
        assertThat(response.getStatus()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Registration successful");
        assertThat(response.getUserData().getEmail()).isEqualTo("user@example.com");
        assertThat(response.getUserData().getUserType()).isEqualTo("user");
        assertThat(response.getUserData().getId()).isNotNull();
        verify(userRepository).existsByEmail("user@example.com");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void register_whenEmailExists_throwsAuth001() {
        RegisterRequest request = RegisterRequest.builder().email("dup@example.com").password("secret").build();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request)).isInstanceOf(GlobalException.class)
                .hasMessage("User already exists")
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("AUTH001");
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success_byEmail() {
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .username("user@example.com")
                .passwordHash("secret")
                .userType(UserTypeEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();
        user.setId(id);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        LoginResponse response = (LoginResponse) (authService.login(LoginRequest.builder()
                .email("user@example.com")
                .password("secret")
                .build())).getData();

        assertThat(response.getStatus()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Login successful");
        assertThat(response.getUserData().getApiToken()).isNotBlank();
        verify(userRepository).save(user);
    }

    @Test
    void login_whenUserMissing_throwsAuth002() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(LoginRequest.builder()
                .email("missing@example.com")
                .password("secret")
                .build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("AUTH002");
    }

    @Test
    void login_whenPasswordInvalid_throwsAuth003() {
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .username("user")
                .passwordHash("correct")
                .userType(UserTypeEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();
        user.setId(UUID.randomUUID());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(LoginRequest.builder()
                .email("user@example.com")
                .password("wrong")
                .build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("AUTH003");
        verify(userRepository, never()).save(any());
    }

    @Test
    void socialLogin_createsUserWhenMissing() {
        when(userRepository.findByEmail("social@example.com")).thenReturn(Optional.empty());

        LoginResponse response = (LoginResponse) (authService.socialLogin(LoginRequest.builder()
                .email("social@example.com")
                .firstName("Soc")
                .lastName("Ial")
                .loginType("google")
                .uid("google-uid")
                .build())).getData();

        assertThat(response.getMessage()).isEqualTo("Social login successful");
        assertThat(response.getUserData().getEmail()).isEqualTo("social@example.com");
        assertThat(response.getUserData().getEmailVerified()).isTrue();
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void socialLogin_updatesExistingUser() {
        UserEntity existing = UserEntity.builder()
                .email("social@example.com")
                .username("social@example.com")
                .passwordHash("hash")
                .userType(UserTypeEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();
        existing.setId(UUID.randomUUID());
        when(userRepository.findByEmail("social@example.com")).thenReturn(Optional.of(existing));

        LoginResponse response = (LoginResponse) (authService.socialLogin(
                LoginRequest.builder().email("social@example.com").uid("new-uid").build())).getData();

        assertThat(response.getMessage()).isEqualTo("Social login successful");
        assertThat(existing.getUid()).isEqualTo("new-uid");
        assertThat(existing.getApiToken()).isNotBlank();
        verify(userRepository).save(existing);
    }

    @Test
    void updateProfile_success() {
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .email("old@example.com")
                .username("old")
                .passwordHash("hash")
                .firstName("Old")
                .userType(UserTypeEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponse response = (UserResponse) (authService.updateProfile(UpdateProfileRequest.builder()
                .id(id)
                .firstName("New")
                .displayName("Display")
                .build())).getData();

        assertThat(response.getFirstName()).isEqualTo("New");
        assertThat(response.getDisplayName()).isEqualTo("Display");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_whenMissing_throwsAuth002() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateProfile(UpdateProfileRequest.builder().id(id).build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("AUTH002");
    }

    @Test
    void changePassword_whenBlank_throwsAuth004() {
        assertThatThrownBy(() -> authService.changePassword(ChangePasswordRequest.builder()
                .oldPassword("old")
                .newPassword("  ")
                .build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("AUTH004");
    }

    @Test
    void changePassword_success() {
        authService.changePassword(ChangePasswordRequest.builder().oldPassword("old").newPassword("new-pass").build());
    }

    @Test
    void forgotPassword_whenMissing_throwsAuth002() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.forgotPassword(ForgotPasswordRequest.builder()
                .email("missing@example.com")
                .build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("AUTH002");
    }

    @Test
    void forgotPassword_success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(UserEntity.builder()
                .email("user@example.com")
                .username("u")
                .passwordHash("h")
                .userType(UserTypeEnum.USER)
                .build()));

        authService.forgotPassword(ForgotPasswordRequest.builder().email("user@example.com").build());
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void deleteUserAccount_marksDeleted() {
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .username("user")
                .passwordHash("hash")
                .userType(UserTypeEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        authService.deleteUserAccount(id);

        assertThat(user.getStatus()).isEqualTo(StatusEnum.DELETED);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserAccount_whenMissing_throwsAuth002() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.deleteUserAccount(id)).isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("AUTH002");
    }

    @Test
    void verifyEmail_success() {
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .username("user")
                .passwordHash("hash")
                .userType(UserTypeEnum.USER)
                .emailVerified(false)
                .build();
        user.setId(UUID.randomUUID());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        authService.verifyEmail(EmailVerifyRequest.builder().email("user@example.com").build());

        assertThat(user.getEmailVerified()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void logout_clearsApiToken() {
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .username("user")
                .passwordHash("hash")
                .userType(UserTypeEnum.USER)
                .apiToken("token")
                .build();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        authService.logout(id);

        assertThat(user.getApiToken()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void getUserDetail_success() {
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .username("user")
                .passwordHash("hash")
                .userType(UserTypeEnum.PROVIDER)
                .status(StatusEnum.ACTIVE)
                .build();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponse response = (UserResponse) authService.getUserDetail(id).getData();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getUserType()).isEqualTo("provider");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void getUserDetail_whenMissing_throwsAuth002() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getUserDetail(id)).isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("AUTH002");
    }
}
