package com.nepbrain.technology.provider.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.provider.payload.request.FavouriteProviderRequest;
import com.nepbrain.technology.provider.payload.request.HandymanRatingRequest;
import com.nepbrain.technology.provider.payload.response.HandymanRatingResponse;
import com.nepbrain.technology.provider.service.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProviderControllerTest {

    @Mock private ProviderService providerService;

    @InjectMocks private ProviderController providerController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(providerController).build();
    }

    @Test
    void userList_returnsOk() throws Exception {
        when(providerService.userList("provider")).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(UserResponse.builder()
                .id(UUID.randomUUID())
                .email("p@example.com")
                .userType("provider")
                .build())).build());

        mockMvc.perform(get("/api/v2/provider/user-list").param("user_type", "provider")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data[0].email").value("p@example.com"));

        verify(providerService).userList(eq("provider"));
    }

    @Test
    void userDetail_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(providerService.providerDetail(id))
                .thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(UserResponse.builder().id(id).email("detail@example.com").build()).build());

        mockMvc.perform(get("/api/v2/provider/user-detail").param("id", id.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("detail@example.com"));

        verify(providerService).providerDetail(eq(id));
    }

    @Test
    void saveFavouriteProvider_returnsSuccessMessage() throws Exception {
        when(providerService.saveFavouriteProvider(any(FavouriteProviderRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Favourite provider saved").build());

        mockMvc.perform(post("/api/v2/provider/save-favourite-provider").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(FavouriteProviderRequest.builder()
                .userId(UUID.randomUUID())
                .providerId(UUID.randomUUID())
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Favourite provider saved"));

        verify(providerService).saveFavouriteProvider(any(FavouriteProviderRequest.class));
    }

    @Test
    void deleteFavouriteProvider_returnsSuccessMessage() throws Exception {
        when(providerService.deleteFavouriteProvider(any(FavouriteProviderRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Favourite provider deleted").build());

        mockMvc.perform(post("/api/v2/provider/delete-favourite-provider").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(FavouriteProviderRequest.builder()
                .userId(UUID.randomUUID())
                .providerId(UUID.randomUUID())
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Favourite provider deleted"));

        verify(providerService).deleteFavouriteProvider(any(FavouriteProviderRequest.class));
    }

    @Test
    void userFavouriteProvider_withUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        when(providerService.userFavouriteProvider(userId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(UserResponse.builder()
                .id(UUID.randomUUID())
                .email("fav@example.com")
                .build())).build());

        mockMvc.perform(get("/api/v2/provider/user-favourite-provider").param("user_id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("fav@example.com"));

        verify(providerService).userFavouriteProvider(eq(userId));
    }

    @Test
    void userFavouriteProvider_withoutUserId() throws Exception {
        when(providerService.userFavouriteProvider(null)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of()).build());

        mockMvc.perform(get("/api/v2/provider/user-favourite-provider")).andExpect(status().isOk());

        verify(providerService).userFavouriteProvider(isNull());
    }

    @Test
    void saveHandymanRating_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        UUID handymanId = UUID.randomUUID();
        when(providerService.saveHandymanRating(any(HandymanRatingRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(
                HandymanRatingResponse.builder()
                .id(id)
                .handymanId(handymanId)
                .rating(4.5)
                .review("Good")
                .build()).build());

        mockMvc.perform(post("/api/v2/provider/save-handyman-rating").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(HandymanRatingRequest.builder()
                .handymanId(handymanId)
                .rating(4.5)
                .review("Good")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rating").value(4.5));

        verify(providerService).saveHandymanRating(any(HandymanRatingRequest.class));
    }

    @Test
    void handymanReviews_returnsOk() throws Exception {
        UUID handymanId = UUID.randomUUID();
        when(providerService.handymanReviews(handymanId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(HandymanRatingResponse.builder()
                .handymanId(handymanId)
                .rating(5.0)
                .review("Great")
                .build())).build());

        mockMvc.perform(post("/api/v2/provider/handyman-reviews").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(HandymanRatingRequest.builder()
                .handymanId(handymanId)
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].review").value("Great"));

        verify(providerService).handymanReviews(eq(handymanId));
    }

    @Test
    void deleteHandymanRating_returnsSuccessMessage() throws Exception {
        UUID id = UUID.randomUUID();
        when(providerService.deleteHandymanRating(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Rating deleted").build());

        mockMvc.perform(post("/api/v2/provider/delete-handyman-rating").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("id", id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rating deleted"));

        verify(providerService).deleteHandymanRating(eq(id));
    }
}
