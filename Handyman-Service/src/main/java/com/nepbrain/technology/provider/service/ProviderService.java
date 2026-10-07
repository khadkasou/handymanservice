package com.nepbrain.technology.provider.service;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.provider.payload.request.*;
import com.nepbrain.technology.provider.payload.response.HandymanRatingResponse;
import java.util.List; import java.util.UUID;
public interface ProviderService {
    List<UserResponse> userList(String userType);
    UserResponse providerDetail(UUID id);
    void saveFavouriteProvider(FavouriteProviderRequest request);
    void deleteFavouriteProvider(FavouriteProviderRequest request);
    List<UserResponse> userFavouriteProvider(UUID userId);
    HandymanRatingResponse saveHandymanRating(HandymanRatingRequest request);
    List<HandymanRatingResponse> handymanReviews(UUID handymanId);
    void deleteHandymanRating(UUID id);
}
