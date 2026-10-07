package com.nepbrain.technology.catalog.service.impl;
import com.nepbrain.technology.catalog.entity.*;
import com.nepbrain.technology.catalog.payload.request.*;
import com.nepbrain.technology.catalog.payload.response.*;
import com.nepbrain.technology.catalog.repository.*;
import com.nepbrain.technology.catalog.service.CatalogService;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;
import static com.nepbrain.technology.catalog.enums.ErrorCodeEnum.*;

@Service @RequiredArgsConstructor @Transactional
public class CatalogServiceImpl implements CatalogService {
    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final FavouriteServiceRepository favouriteServiceRepository;
    private final ModelMapper modelMapper;

    @Override @Transactional(readOnly = true)
    public List<CategoryResponse> categoryList() {
        return categoryRepository.findByParentIdIsNull().stream().map(this::toCat).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public List<CategoryResponse> subcategoryList(UUID categoryId) {
        return categoryRepository.findByParentId(categoryId).stream().map(this::toCat).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public List<ServiceResponse> serviceList(UUID customerId) {
        return serviceRepository.findAll().stream().map(this::toSvc).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public ServiceResponse serviceDetail(ServiceRequest request) {
        return toSvc(serviceRepository.findById(request.getId()).orElseThrow(() -> new GlobalException(SER001.name())));
    }
    @Override @Transactional(readOnly = true)
    public List<ServiceResponse> searchList(String search, UUID categoryId) {
        List<ServiceEntity> list = search != null && !search.isBlank()
                ? serviceRepository.findByNameContainingIgnoreCase(search) : serviceRepository.findAll();
        if (categoryId != null) list = list.stream().filter(s -> categoryId.equals(s.getCategoryId())).collect(Collectors.toList());
        return list.stream().map(this::toSvc).collect(Collectors.toList());
    }
    @Override public void saveFavourite(FavouriteRequest request) {
        favouriteServiceRepository.findByUserIdAndServiceId(request.getUserId(), request.getServiceId())
                .orElseGet(() -> favouriteServiceRepository.save(FavouriteServiceEntity.builder()
                        .userId(request.getUserId()).serviceId(request.getServiceId()).build()));
    }
    @Override public void deleteFavourite(FavouriteRequest request) {
        favouriteServiceRepository.deleteByUserIdAndServiceId(request.getUserId(), request.getServiceId());
    }
    @Override @Transactional(readOnly = true)
    public List<ServiceResponse> userFavouriteService(UUID userId) {
        return favouriteServiceRepository.findByUserId(userId).stream()
                .map(f -> serviceRepository.findById(f.getServiceId()).orElse(null))
                .filter(s -> s != null).map(this::toSvc).collect(Collectors.toList());
    }
    private CategoryResponse toCat(CategoryEntity e) {
        CategoryResponse r = modelMapper.map(e, CategoryResponse.class);
        r.setStatus(e.getStatus() != null ? e.getStatus().name() : null); return r;
    }
    private ServiceResponse toSvc(ServiceEntity e) {
        ServiceResponse r = modelMapper.map(e, ServiceResponse.class);
        r.setStatus(e.getStatus() != null ? e.getStatus().name() : null); return r;
    }
}
