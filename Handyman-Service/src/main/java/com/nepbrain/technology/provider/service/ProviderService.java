package com.nepbrain.technology.provider.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.provider.payload.request.*;

import java.util.UUID;

public interface ProviderService {

    GlobalResponse userList(String userType);

    GlobalResponse providerDetail(UUID id);

    GlobalResponse saveFavouriteProvider(FavouriteProviderRequest request);

    GlobalResponse changeFavouriteProviderStatus(FavouriteProviderRequest request, StatusEnum status);

    GlobalResponse deleteFavouriteProvider(FavouriteProviderRequest request);

    GlobalResponse userFavouriteProvider(UUID userId);

    GlobalResponse saveHandymanRating(HandymanRatingRequest request);

    GlobalResponse handymanReviews(UUID handymanId);

    GlobalResponse changeHandymanRatingStatus(UUID id, StatusEnum status);

    GlobalResponse deleteHandymanRating(UUID id);
}
