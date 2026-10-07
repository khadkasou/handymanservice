package com.nepbrain.technology.catalog.service;

import com.nepbrain.technology.catalog.entity.CategoryEntity;
import com.nepbrain.technology.catalog.entity.FavouriteServiceEntity;
import com.nepbrain.technology.catalog.entity.ServiceEntity;
import com.nepbrain.technology.catalog.payload.request.FavouriteRequest;
import com.nepbrain.technology.catalog.payload.request.ServiceRequest;
import com.nepbrain.technology.catalog.payload.response.CategoryResponse;
import com.nepbrain.technology.catalog.payload.response.ServiceResponse;
import com.nepbrain.technology.catalog.repository.CategoryRepository;
import com.nepbrain.technology.catalog.repository.FavouriteServiceRepository;
import com.nepbrain.technology.catalog.repository.ServiceRepository;
import com.nepbrain.technology.catalog.service.impl.CatalogServiceImpl;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private FavouriteServiceRepository favouriteServiceRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CatalogServiceImpl catalogService;

    @BeforeEach
    void stubFavouriteSave() {
        lenient().when(favouriteServiceRepository.save(any(FavouriteServiceEntity.class)))
                .thenAnswer(invocation -> {
                    FavouriteServiceEntity entity = invocation.getArgument(0);
                    if (entity.getId() == null) {
                        entity.setId(UUID.randomUUID());
                    }
                    return entity;
                });
    }

    @Test
    void categoryList_returnsRootCategories() {
        CategoryEntity category = CategoryEntity.builder()
                .name("Plumbing")
                .status(StatusEnum.ACTIVE)
                .build();
        category.setId(UUID.randomUUID());
        when(categoryRepository.findByParentIdIsNull()).thenReturn(List.of(category));

        List<CategoryResponse> list = catalogService.categoryList();

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getName()).isEqualTo("Plumbing");
        assertThat(list.getFirst().getStatus()).isEqualTo("ACTIVE");
        verify(categoryRepository).findByParentIdIsNull();
    }

    @Test
    void subcategoryList_filtersByParent() {
        UUID parentId = UUID.randomUUID();
        CategoryEntity sub = CategoryEntity.builder()
                .name("Pipe Repair")
                .parentId(parentId)
                .status(StatusEnum.ACTIVE)
                .build();
        sub.setId(UUID.randomUUID());
        when(categoryRepository.findByParentId(parentId)).thenReturn(List.of(sub));

        List<CategoryResponse> list = catalogService.subcategoryList(parentId);

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getName()).isEqualTo("Pipe Repair");
        verify(categoryRepository).findByParentId(parentId);
    }

    @Test
    void serviceList_returnsAll() {
        ServiceEntity service = ServiceEntity.builder()
                .name("AC Repair")
                .price(new BigDecimal("999"))
                .status(StatusEnum.ACTIVE)
                .build();
        service.setId(UUID.randomUUID());
        when(serviceRepository.findAll()).thenReturn(List.of(service));

        List<ServiceResponse> list = catalogService.serviceList(UUID.randomUUID());

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getName()).isEqualTo("AC Repair");
        assertThat(list.getFirst().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void serviceDetail_success() {
        UUID id = UUID.randomUUID();
        ServiceEntity service = ServiceEntity.builder()
                .name("Cleaning")
                .status(StatusEnum.ACTIVE)
                .build();
        service.setId(id);
        when(serviceRepository.findById(id)).thenReturn(Optional.of(service));

        ServiceResponse response = catalogService.serviceDetail(ServiceRequest.builder().id(id).build());

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Cleaning");
    }

    @Test
    void serviceDetail_whenMissing_throwsSer001() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogService.serviceDetail(ServiceRequest.builder().id(id).build()))
                .isInstanceOf(GlobalException.class)
                .hasMessage("Service not found")
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("SER001");
    }

    @Test
    void searchList_byNameAndCategory() {
        UUID categoryId = UUID.randomUUID();
        ServiceEntity match = ServiceEntity.builder()
                .name("Deep Clean")
                .categoryId(categoryId)
                .status(StatusEnum.ACTIVE)
                .build();
        match.setId(UUID.randomUUID());
        ServiceEntity other = ServiceEntity.builder()
                .name("Deep Clean Extra")
                .categoryId(UUID.randomUUID())
                .status(StatusEnum.ACTIVE)
                .build();
        other.setId(UUID.randomUUID());
        when(serviceRepository.findByNameContainingIgnoreCase("Deep"))
                .thenReturn(List.of(match, other));

        List<ServiceResponse> list = catalogService.searchList("Deep", categoryId);

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getName()).isEqualTo("Deep Clean");
    }

    @Test
    void searchList_blankSearch_returnsAllFilteredByCategory() {
        UUID categoryId = UUID.randomUUID();
        ServiceEntity match = ServiceEntity.builder()
                .name("A")
                .categoryId(categoryId)
                .status(StatusEnum.ACTIVE)
                .build();
        match.setId(UUID.randomUUID());
        when(serviceRepository.findAll()).thenReturn(List.of(match));

        List<ServiceResponse> list = catalogService.searchList("  ", categoryId);

        assertThat(list).hasSize(1);
        verify(serviceRepository).findAll();
    }

    @Test
    void saveFavourite_createsWhenMissing() {
        UUID userId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        when(favouriteServiceRepository.findByUserIdAndServiceId(userId, serviceId))
                .thenReturn(Optional.empty());

        catalogService.saveFavourite(FavouriteRequest.builder()
                .userId(userId)
                .serviceId(serviceId)
                .build());

        verify(favouriteServiceRepository).save(any(FavouriteServiceEntity.class));
    }

    @Test
    void saveFavourite_skipsWhenAlreadyExists() {
        UUID userId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        FavouriteServiceEntity existing = FavouriteServiceEntity.builder()
                .userId(userId)
                .serviceId(serviceId)
                .build();
        existing.setId(UUID.randomUUID());
        when(favouriteServiceRepository.findByUserIdAndServiceId(userId, serviceId))
                .thenReturn(Optional.of(existing));

        catalogService.saveFavourite(FavouriteRequest.builder()
                .userId(userId)
                .serviceId(serviceId)
                .build());

        verify(favouriteServiceRepository, never()).save(any());
    }

    @Test
    void deleteFavourite_delegatesToRepository() {
        UUID userId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        catalogService.deleteFavourite(FavouriteRequest.builder()
                .userId(userId)
                .serviceId(serviceId)
                .build());

        verify(favouriteServiceRepository).deleteByUserIdAndServiceId(userId, serviceId);
    }

    @Test
    void userFavouriteService_resolvesServices() {
        UUID userId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        FavouriteServiceEntity fav = FavouriteServiceEntity.builder()
                .userId(userId)
                .serviceId(serviceId)
                .build();
        fav.setId(UUID.randomUUID());
        ServiceEntity service = ServiceEntity.builder()
                .name("Fav Service")
                .status(StatusEnum.ACTIVE)
                .build();
        service.setId(serviceId);
        when(favouriteServiceRepository.findByUserId(userId)).thenReturn(List.of(fav));
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        List<ServiceResponse> list = catalogService.userFavouriteService(userId);

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getName()).isEqualTo("Fav Service");
    }

    @Test
    void userFavouriteService_skipsMissingService() {
        UUID userId = UUID.randomUUID();
        UUID missingServiceId = UUID.randomUUID();
        FavouriteServiceEntity fav = FavouriteServiceEntity.builder()
                .userId(userId)
                .serviceId(missingServiceId)
                .build();
        fav.setId(UUID.randomUUID());
        when(favouriteServiceRepository.findByUserId(userId)).thenReturn(List.of(fav));
        when(serviceRepository.findById(missingServiceId)).thenReturn(Optional.empty());

        assertThat(catalogService.userFavouriteService(userId)).isEmpty();
    }
}
