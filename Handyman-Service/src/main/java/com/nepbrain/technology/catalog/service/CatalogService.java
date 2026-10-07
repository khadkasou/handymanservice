package com.nepbrain.technology.catalog.service;
import com.nepbrain.technology.catalog.payload.request.*;
import com.nepbrain.technology.catalog.payload.response.*;
import java.util.List; import java.util.UUID;
public interface CatalogService {
    List<CategoryResponse> categoryList();
    List<CategoryResponse> subcategoryList(UUID categoryId);
    List<ServiceResponse> serviceList(UUID customerId);
    ServiceResponse serviceDetail(ServiceRequest request);
    List<ServiceResponse> searchList(String search, UUID categoryId);
    void saveFavourite(FavouriteRequest request);
    void deleteFavourite(FavouriteRequest request);
    List<ServiceResponse> userFavouriteService(UUID userId);
}
