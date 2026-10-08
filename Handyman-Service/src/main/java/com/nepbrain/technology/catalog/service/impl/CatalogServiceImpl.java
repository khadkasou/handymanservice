package com.nepbrain.technology.catalog.service.impl;

import com.nepbrain.technology.catalog.entity.CategoryEntity;
import com.nepbrain.technology.catalog.entity.FavouriteServiceEntity;
import com.nepbrain.technology.catalog.entity.ServiceEntity;
import com.nepbrain.technology.catalog.enums.ErrorCodeEnum;
import com.nepbrain.technology.catalog.payload.request.FavouriteRequest;
import com.nepbrain.technology.catalog.payload.request.ServiceRequest;
import com.nepbrain.technology.catalog.payload.response.CategoryResponse;
import com.nepbrain.technology.catalog.payload.response.ServiceResponse;
import com.nepbrain.technology.catalog.repository.CategoryRepository;
import com.nepbrain.technology.catalog.repository.FavouriteServiceRepository;
import com.nepbrain.technology.catalog.repository.ServiceRepository;
import com.nepbrain.technology.catalog.service.CatalogService;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final FavouriteServiceRepository favouriteServiceRepository;

    @Override
    public GlobalResponse categoryList() {
        List<CategoryResponse> categories = new ArrayList<>();
        for (CategoryEntity category : categoryRepository.findByParentIdIsNull()) {
            categories.add(toCategory(category));
        }
        return GlobalResponseBuilder.buildSuccessResponse(categories);
    }

    @Override
    public GlobalResponse subcategoryList(UUID categoryId) {
        List<CategoryResponse> subcategories = new ArrayList<>();
        for (CategoryEntity category : categoryRepository.findByParentId(categoryId)) {
            subcategories.add(toCategory(category));
        }
        return GlobalResponseBuilder.buildSuccessResponse(subcategories);
    }

    @Override
    public GlobalResponse serviceList(UUID customerId) {
        List<ServiceResponse> services = new ArrayList<>();
        for (ServiceEntity service : serviceRepository.findAll()) {
            services.add(toService(service));
        }
        return GlobalResponseBuilder.buildSuccessResponse(services);
    }

    @Override
    public GlobalResponse serviceDetail(ServiceRequest request) {
        ServiceEntity service = serviceRepository.findById(request.getId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SER001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(toService(service));
    }

    @Override
    public GlobalResponse searchList(String search, UUID categoryId) {
        List<ServiceEntity> matches;
        if (search != null && !search.isBlank()) {
            matches = serviceRepository.findByNameContainingIgnoreCase(search);
        } else {
            matches = serviceRepository.findAll();
        }

        List<ServiceResponse> services = new ArrayList<>();
        for (ServiceEntity service : matches) {
            if (categoryId != null && !categoryId.equals(service.getCategoryId())) {
                continue;
            }
            services.add(toService(service));
        }
        return GlobalResponseBuilder.buildSuccessResponse(services);
    }

    @Override
    public GlobalResponse saveFavourite(FavouriteRequest request) {
        FavouriteServiceEntity favourite = favouriteServiceRepository
                .findByUserIdAndServiceId(request.getUserId(), request.getServiceId())
                .orElse(null);
        if (favourite != null) {
            if (favourite.getStatus() == StatusEnum.INACTIVE) {
                favourite.setStatus(StatusEnum.ACTIVE);
                favouriteServiceRepository.save(favourite);
            }
            return GlobalResponseBuilder.buildSuccessResponse("message.catalog.favourite.saved.success");
        }
        favourite = new FavouriteServiceEntity();
        favourite.setUserId(request.getUserId());
        favourite.setServiceId(request.getServiceId());
        favourite.setStatus(StatusEnum.ACTIVE);
        favouriteServiceRepository.save(favourite);
        return GlobalResponseBuilder.buildSuccessResponse("message.catalog.favourite.saved.success");
    }

    @Override
    public GlobalResponse changeFavouriteStatus(FavouriteRequest request, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.FAV002.name());
        }
        FavouriteServiceEntity favourite = favouriteServiceRepository
                .findByUserIdAndServiceId(request.getUserId(), request.getServiceId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.FAV001.name(), HttpStatus.BAD_REQUEST));
        favourite.setStatus(status);
        favouriteServiceRepository.save(favourite);
        return GlobalResponseBuilder.buildSuccessResponse("message.catalog.favourite.status.success");
    }

    @Override
    public GlobalResponse deleteFavourite(FavouriteRequest request) {
        return changeFavouriteStatus(request, StatusEnum.INACTIVE);
    }

    @Override
    public GlobalResponse userFavouriteService(UUID userId) {
        List<ServiceResponse> services = new ArrayList<>();
        for (FavouriteServiceEntity favourite : favouriteServiceRepository.findByUserId(userId)) {
            if (favourite.getStatus() == StatusEnum.INACTIVE) {
                continue;
            }
            ServiceEntity service = serviceRepository.findById(favourite.getServiceId()).orElse(null);
            if (service == null) {
                continue;
            }
            services.add(toService(service));
        }
        return GlobalResponseBuilder.buildSuccessResponse(services);
    }

    private CategoryResponse toCategory(CategoryEntity category) {
        String status = category.getStatus() == null ? null : category.getStatus().name();
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .image(category.getImage())
                .parentId(category.getParentId())
                .status(status)
                .isFeatured(category.getIsFeatured())
                .build();
    }

    private ServiceResponse toService(ServiceEntity service) {
        String status = service.getStatus() == null ? null : service.getStatus().name();
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .categoryId(service.getCategoryId())
                .subcategoryId(service.getSubcategoryId())
                .providerId(service.getProviderId())
                .price(service.getPrice())
                .discount(service.getDiscount())
                .duration(service.getDuration())
                .image(service.getImage())
                .status(status)
                .isFeatured(service.getIsFeatured())
                .serviceType(service.getServiceType())
                .build();
    }
}
