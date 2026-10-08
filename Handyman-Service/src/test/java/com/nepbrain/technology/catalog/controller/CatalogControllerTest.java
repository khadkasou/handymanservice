package com.nepbrain.technology.catalog.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.catalog.payload.request.FavouriteRequest;
import com.nepbrain.technology.catalog.payload.request.ServiceRequest;
import com.nepbrain.technology.catalog.payload.response.CategoryResponse;
import com.nepbrain.technology.catalog.payload.response.ServiceResponse;
import com.nepbrain.technology.catalog.service.CatalogService;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    @Mock private CatalogService catalogService;

    @InjectMocks private CatalogController catalogController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(catalogController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void categoryList_returnsSuccess() throws Exception {
        when(catalogService.categoryList()).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(CategoryResponse.builder()
                .id(UUID.randomUUID())
                .name("Plumbing")
                .build())).build());

        mockMvc.perform(get("/api/v2/catalog/category-list")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).categoryList();
    }

    @Test
    void subcategoryList_returnsSuccess() throws Exception {
        UUID categoryId = UUID.randomUUID();
        when(catalogService.subcategoryList(categoryId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(CategoryResponse.builder()
                .id(UUID.randomUUID())
                .name("Pipes")
                .parentId(categoryId)
                .build())).build());

        mockMvc.perform(get("/api/v2/catalog/subcategory-list").param("category_id", categoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).subcategoryList(categoryId);
    }

    @Test
    void serviceList_returnsSuccess() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(catalogService.serviceList(customerId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(ServiceResponse.builder()
                .id(UUID.randomUUID())
                .name("Fix")
                .price(BigDecimal.TEN)
                .build())).build());

        mockMvc.perform(get("/api/v2/catalog/service-list").param("customer_id", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).serviceList(customerId);
    }

    @Test
    void serviceDetail_returnsSuccess() throws Exception {
        UUID serviceId = UUID.randomUUID();
        ServiceRequest request = ServiceRequest.builder().id(serviceId).build();
        when(catalogService.serviceDetail(any(ServiceRequest.class)))
                .thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(ServiceResponse.builder().id(serviceId).name("Detail").build()).build());

        mockMvc.perform(post("/api/v2/catalog/service-detail").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).serviceDetail(any(ServiceRequest.class));
    }

    @Test
    void searchList_returnsSuccess() throws Exception {
        UUID categoryId = UUID.randomUUID();
        when(catalogService.searchList(eq("pipe"), eq(categoryId))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(ServiceResponse.builder()
                .id(UUID.randomUUID())
                .name("Pipe Fix")
                .build())).build());

        mockMvc.perform(get("/api/v2/catalog/search-list").param("search", "pipe")
                .param("category_id", categoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).searchList("pipe", categoryId);
    }

    @Test
    void saveFavourite_returnsSuccess() throws Exception {
        FavouriteRequest request = FavouriteRequest.builder()
                .userId(UUID.randomUUID())
                .serviceId(UUID.randomUUID())
                .build();
        when(catalogService.saveFavourite(any(FavouriteRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).build());

        mockMvc.perform(post("/api/v2/catalog/save-favourite").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).saveFavourite(any(FavouriteRequest.class));
    }

    @Test
    void deleteFavourite_returnsSuccess() throws Exception {
        FavouriteRequest request = FavouriteRequest.builder()
                .userId(UUID.randomUUID())
                .serviceId(UUID.randomUUID())
                .build();
        when(catalogService.deleteFavourite(any(FavouriteRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).build());

        mockMvc.perform(post("/api/v2/catalog/delete-favourite").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).deleteFavourite(any(FavouriteRequest.class));
    }

    @Test
    void userFavouriteService_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        when(catalogService.userFavouriteService(userId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(ServiceResponse.builder()
                .id(UUID.randomUUID())
                .name("Fav")
                .build())).build());

        mockMvc.perform(get("/api/v2/catalog/user-favourite-service").param("user_id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).userFavouriteService(userId);
    }

    @Test
    void serviceList_withoutCustomerId_returnsSuccess() throws Exception {
        when(catalogService.serviceList(isNull())).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of()).build());

        mockMvc.perform(get("/api/v2/catalog/service-list")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(catalogService).serviceList(null);
    }
}
