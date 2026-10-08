package com.nepbrain.technology.provider.service.impl;

import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.provider.entity.FavouriteProviderEntity;
import com.nepbrain.technology.provider.entity.HandymanRatingEntity;
import com.nepbrain.technology.provider.enums.ErrorCodeEnum;
import com.nepbrain.technology.provider.payload.request.FavouriteProviderRequest;
import com.nepbrain.technology.provider.payload.request.HandymanRatingRequest;
import com.nepbrain.technology.provider.payload.response.HandymanRatingResponse;
import com.nepbrain.technology.provider.repository.FavouriteProviderRepository;
import com.nepbrain.technology.provider.repository.HandymanRatingRepository;
import com.nepbrain.technology.provider.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final UserRepository userRepository;
    private final FavouriteProviderRepository favouriteProviderRepository;
    private final HandymanRatingRepository handymanRatingRepository;

    @Override
    public GlobalResponse userList(String userType) {
        UserTypeEnum type = UserTypeEnum.valueOf(userType.trim().toUpperCase());
        List<UserResponse> users = new ArrayList<>();
        for (UserEntity user : userRepository.findByUserType(type)) {
            users.add(toUser(user));
        }
        return GlobalResponseBuilder.buildSuccessResponse(users);
    }

    @Override
    public GlobalResponse providerDetail(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PRV001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(toUser(user));
    }

    @Override
    public GlobalResponse saveFavouriteProvider(FavouriteProviderRequest request) {
        FavouriteProviderEntity favourite = favouriteProviderRepository
                .findByUserIdAndProviderId(request.getUserId(), request.getProviderId())
                .orElse(null);
        if (favourite != null) {
            if (favourite.getStatus() == StatusEnum.INACTIVE) {
                favourite.setStatus(StatusEnum.ACTIVE);
                favouriteProviderRepository.save(favourite);
            }
            return GlobalResponseBuilder.buildSuccessResponse("message.provider.favourite.saved.success");
        }
        favourite = new FavouriteProviderEntity();
        favourite.setUserId(request.getUserId());
        favourite.setProviderId(request.getProviderId());
        favourite.setStatus(StatusEnum.ACTIVE);
        favouriteProviderRepository.save(favourite);
        return GlobalResponseBuilder.buildSuccessResponse("message.provider.favourite.saved.success");
    }

    @Override
    public GlobalResponse changeFavouriteProviderStatus(FavouriteProviderRequest request, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.PRV004.name());
        }
        FavouriteProviderEntity favourite = favouriteProviderRepository
                .findByUserIdAndProviderId(request.getUserId(), request.getProviderId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PRV002.name(), HttpStatus.BAD_REQUEST));
        favourite.setStatus(status);
        favouriteProviderRepository.save(favourite);
        return GlobalResponseBuilder.buildSuccessResponse("message.provider.favourite.status.success");
    }

    @Override
    public GlobalResponse deleteFavouriteProvider(FavouriteProviderRequest request) {
        return changeFavouriteProviderStatus(request, StatusEnum.INACTIVE);
    }

    @Override
    public GlobalResponse userFavouriteProvider(UUID userId) {
        List<UserResponse> providers = new ArrayList<>();
        for (FavouriteProviderEntity favourite : favouriteProviderRepository.findByUserId(userId)) {
            if (favourite.getStatus() == StatusEnum.INACTIVE) {
                continue;
            }
            UserEntity provider = userRepository.findById(favourite.getProviderId()).orElse(null);
            if (provider == null) {
                continue;
            }
            providers.add(toUser(provider));
        }
        return GlobalResponseBuilder.buildSuccessResponse(providers);
    }

    @Override
    public GlobalResponse saveHandymanRating(HandymanRatingRequest request) {
        HandymanRatingEntity rating = new HandymanRatingEntity();
        if (request.getId() != null) {
            rating.setId(request.getId());
        }
        rating.setHandymanId(request.getHandymanId());
        rating.setCustomerId(request.getCustomerId());
        rating.setBookingId(request.getBookingId());
        rating.setRating(request.getRating());
        rating.setReview(request.getReview());
        if (rating.getStatus() == null) {
            rating.setStatus(StatusEnum.ACTIVE);
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.provider.rating.saved.success", toRatingResponse(handymanRatingRepository.save(rating)));
    }

    @Override
    public GlobalResponse handymanReviews(UUID handymanId) {
        List<HandymanRatingResponse> reviews = new ArrayList<>();
        for (HandymanRatingEntity rating : handymanRatingRepository.findByHandymanId(handymanId)) {
            if (rating.getStatus() == StatusEnum.INACTIVE) {
                continue;
            }
            reviews.add(toRatingResponse(rating));
        }
        return GlobalResponseBuilder.buildSuccessResponse(reviews);
    }

    @Override
    public GlobalResponse changeHandymanRatingStatus(UUID id, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.PRV005.name());
        }
        HandymanRatingEntity rating = handymanRatingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PRV003.name(), HttpStatus.BAD_REQUEST));
        rating.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.provider.rating.status.success", toRatingResponse(handymanRatingRepository.save(rating)));
    }

    @Override
    public GlobalResponse deleteHandymanRating(UUID id) {
        return changeHandymanRatingStatus(id, StatusEnum.INACTIVE);
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

    private HandymanRatingResponse toRatingResponse(HandymanRatingEntity rating) {
        return HandymanRatingResponse.builder()
                .id(rating.getId())
                .handymanId(rating.getHandymanId())
                .customerId(rating.getCustomerId())
                .bookingId(rating.getBookingId())
                .rating(rating.getRating())
                .review(rating.getReview())
                .build();
    }
}
