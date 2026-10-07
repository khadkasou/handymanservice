package com.nepbrain.technology.provider.service;

import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.provider.entity.FavouriteProviderEntity;
import com.nepbrain.technology.provider.entity.HandymanRatingEntity;
import com.nepbrain.technology.provider.payload.request.FavouriteProviderRequest;
import com.nepbrain.technology.provider.payload.request.HandymanRatingRequest;
import com.nepbrain.technology.provider.payload.response.HandymanRatingResponse;
import com.nepbrain.technology.provider.repository.FavouriteProviderRepository;
import com.nepbrain.technology.provider.repository.HandymanRatingRepository;
import com.nepbrain.technology.provider.service.impl.ProviderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FavouriteProviderRepository favouriteProviderRepository;
    @Mock
    private HandymanRatingRepository handymanRatingRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ProviderServiceImpl providerService;

    @BeforeEach
    void stubSaves() {
        lenient().when(favouriteProviderRepository.save(any(FavouriteProviderEntity.class)))
                .thenAnswer(invocation -> {
                    FavouriteProviderEntity entity = invocation.getArgument(0);
                    if (entity.getId() == null) {
                        entity.setId(UUID.randomUUID());
                    }
                    return entity;
                });
        lenient().when(handymanRatingRepository.save(any(HandymanRatingEntity.class)))
                .thenAnswer(invocation -> {
                    HandymanRatingEntity entity = invocation.getArgument(0);
                    if (entity.getId() == null) {
                        entity.setId(UUID.randomUUID());
                    }
                    return entity;
                });
    }

    @Test
    void userList_filtersByUserType() {
        UserEntity provider = UserEntity.builder()
                .email("p@example.com")
                .username("provider1")
                .passwordHash("hash")
                .userType(UserTypeEnum.PROVIDER)
                .status(StatusEnum.ACTIVE)
                .build();
        provider.setId(UUID.randomUUID());
        when(userRepository.findByUserType(UserTypeEnum.PROVIDER)).thenReturn(List.of(provider));

        List<UserResponse> list = providerService.userList("provider");

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getUserType()).isEqualTo("provider");
        verify(userRepository).findByUserType(UserTypeEnum.PROVIDER);
    }

    @Test
    void providerDetail_success() {
        UUID id = UUID.randomUUID();
        UserEntity provider = UserEntity.builder()
                .email("p@example.com")
                .username("provider1")
                .passwordHash("hash")
                .userType(UserTypeEnum.PROVIDER)
                .status(StatusEnum.ACTIVE)
                .build();
        provider.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(provider));

        UserResponse response = providerService.providerDetail(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getEmail()).isEqualTo("p@example.com");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void providerDetail_whenMissing_throwsPrv001() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> providerService.providerDetail(id))
                .isInstanceOf(GlobalException.class)
                .hasMessage("Provider not found")
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PRV001");
    }

    @Test
    void saveFavouriteProvider_createsWhenMissing() {
        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        when(favouriteProviderRepository.findByUserIdAndProviderId(userId, providerId))
                .thenReturn(Optional.empty());

        providerService.saveFavouriteProvider(FavouriteProviderRequest.builder()
                .userId(userId)
                .providerId(providerId)
                .build());

        verify(favouriteProviderRepository).save(any(FavouriteProviderEntity.class));
    }

    @Test
    void saveFavouriteProvider_skipsWhenExists() {
        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        FavouriteProviderEntity existing = FavouriteProviderEntity.builder()
                .userId(userId)
                .providerId(providerId)
                .build();
        existing.setId(UUID.randomUUID());
        when(favouriteProviderRepository.findByUserIdAndProviderId(userId, providerId))
                .thenReturn(Optional.of(existing));

        providerService.saveFavouriteProvider(FavouriteProviderRequest.builder()
                .userId(userId)
                .providerId(providerId)
                .build());

        verify(favouriteProviderRepository, never()).save(any());
    }

    @Test
    void deleteFavouriteProvider_delegates() {
        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();

        providerService.deleteFavouriteProvider(FavouriteProviderRequest.builder()
                .userId(userId)
                .providerId(providerId)
                .build());

        verify(favouriteProviderRepository).deleteByUserIdAndProviderId(userId, providerId);
    }

    @Test
    void userFavouriteProvider_resolvesProviders() {
        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        FavouriteProviderEntity fav = FavouriteProviderEntity.builder()
                .userId(userId)
                .providerId(providerId)
                .build();
        fav.setId(UUID.randomUUID());
        UserEntity provider = UserEntity.builder()
                .email("fav@example.com")
                .username("fav")
                .passwordHash("hash")
                .userType(UserTypeEnum.PROVIDER)
                .status(StatusEnum.ACTIVE)
                .build();
        provider.setId(providerId);
        when(favouriteProviderRepository.findByUserId(userId)).thenReturn(List.of(fav));
        when(userRepository.findById(providerId)).thenReturn(Optional.of(provider));

        List<UserResponse> list = providerService.userFavouriteProvider(userId);

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getEmail()).isEqualTo("fav@example.com");
    }

    @Test
    void saveHandymanRating_success() {
        UUID handymanId = UUID.randomUUID();
        HandymanRatingRequest request = HandymanRatingRequest.builder()
                .handymanId(handymanId)
                .customerId(UUID.randomUUID())
                .bookingId(UUID.randomUUID())
                .rating(4.5)
                .review("Great work")
                .build();

        HandymanRatingResponse response = providerService.saveHandymanRating(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getHandymanId()).isEqualTo(handymanId);
        assertThat(response.getRating()).isEqualTo(4.5);
        assertThat(response.getReview()).isEqualTo("Great work");
        verify(handymanRatingRepository).save(any(HandymanRatingEntity.class));
    }

    @Test
    void handymanReviews_returnsMappedList() {
        UUID handymanId = UUID.randomUUID();
        HandymanRatingEntity rating = HandymanRatingEntity.builder()
                .handymanId(handymanId)
                .customerId(UUID.randomUUID())
                .rating(5.0)
                .review("Excellent")
                .build();
        rating.setId(UUID.randomUUID());
        when(handymanRatingRepository.findByHandymanId(handymanId)).thenReturn(List.of(rating));

        List<HandymanRatingResponse> list = providerService.handymanReviews(handymanId);

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getReview()).isEqualTo("Excellent");
        verify(handymanRatingRepository).findByHandymanId(handymanId);
    }

    @Test
    void deleteHandymanRating_success() {
        UUID id = UUID.randomUUID();
        when(handymanRatingRepository.existsById(id)).thenReturn(true);

        providerService.deleteHandymanRating(id);

        verify(handymanRatingRepository).deleteById(id);
    }

    @Test
    void deleteHandymanRating_whenMissing_throwsPrv003() {
        UUID id = UUID.randomUUID();
        when(handymanRatingRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> providerService.deleteHandymanRating(id))
                .isInstanceOf(GlobalException.class)
                .hasMessage("Rating not found")
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PRV003");
        verify(handymanRatingRepository, never()).deleteById(any());
    }
}
