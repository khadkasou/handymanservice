package com.nepbrain.technology.catalog.service;

import com.nepbrain.technology.catalog.payload.request.*;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface CatalogService {

    GlobalResponse categoryList();

    GlobalResponse subcategoryList(UUID categoryId);

    GlobalResponse serviceList(UUID customerId);

    GlobalResponse serviceDetail(ServiceRequest request);

    GlobalResponse searchList(String search, UUID categoryId);

    GlobalResponse saveFavourite(FavouriteRequest request);

    GlobalResponse changeFavouriteStatus(FavouriteRequest request, StatusEnum status);

    GlobalResponse deleteFavourite(FavouriteRequest request);

    GlobalResponse userFavouriteService(UUID userId);
}
