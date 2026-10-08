package com.nepbrain.technology.provider.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.provider.annotation.CustomProviderRestController;
import com.nepbrain.technology.provider.payload.request.FavouriteProviderRequest;
import com.nepbrain.technology.provider.payload.request.HandymanRatingRequest;
import com.nepbrain.technology.provider.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@CustomProviderRestController
@RequiredArgsConstructor
@Tag(name = "Provider")
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping("/user-list")
    @Operation(summary = "Provider or handyman list")
    public ResponseEntity<GlobalResponse> userList(@RequestParam("user_type") String userType) {
        return ResponseEntity.ok().body(providerService.userList(userType));
    }

    @GetMapping("/user-detail")
    @Operation(summary = "Provider detail")
    public ResponseEntity<GlobalResponse> userDetail(@RequestParam UUID id) {
        return ResponseEntity.ok().body(providerService.providerDetail(id));
    }

    @PostMapping("/save-favourite-provider")
    @Operation(summary = "Save favourite provider")
    public ResponseEntity<GlobalResponse> saveFavouriteProvider(@RequestBody FavouriteProviderRequest request) {
        return ResponseEntity.ok().body(providerService.saveFavouriteProvider(request));
    }

    @PutMapping("/favourite-provider/status")
    @Operation(summary = "Change favourite provider status")
    public ResponseEntity<GlobalResponse> changeFavouriteProviderStatus(@RequestBody FavouriteProviderRequest request,
                                                                        @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(providerService.changeFavouriteProviderStatus(request, status));
    }

    @PostMapping("/delete-favourite-provider")
    @Operation(summary = "Mark favourite provider inactive")
    public ResponseEntity<GlobalResponse> deleteFavouriteProvider(@RequestBody FavouriteProviderRequest request) {
        return ResponseEntity.ok().body(providerService.deleteFavouriteProvider(request));
    }

    @GetMapping("/user-favourite-provider")
    @Operation(summary = "Favourite providers")
    public ResponseEntity<GlobalResponse> userFavouriteProvider(@RequestParam(value = "user_id", required = false) UUID userId) {
        return ResponseEntity.ok().body(providerService.userFavouriteProvider(userId));
    }

    @PostMapping("/save-handyman-rating")
    @Operation(summary = "Save handyman rating")
    public ResponseEntity<GlobalResponse> saveHandymanRating(@RequestBody HandymanRatingRequest request) {
        return ResponseEntity.ok().body(providerService.saveHandymanRating(request));
    }

    @PostMapping("/handyman-reviews")
    @Operation(summary = "Handyman reviews")
    public ResponseEntity<GlobalResponse> handymanReviews(@RequestBody HandymanRatingRequest request) {
        return ResponseEntity.ok().body(providerService.handymanReviews(request.getHandymanId()));
    }

    @PutMapping("/handyman-rating/{id}/status")
    @Operation(summary = "Change handyman rating status")
    public ResponseEntity<GlobalResponse> changeHandymanRatingStatus(@PathVariable UUID id,
                                                                     @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(providerService.changeHandymanRatingStatus(id, status));
    }

    @PostMapping("/delete-handyman-rating")
    @Operation(summary = "Mark handyman rating inactive")
    public ResponseEntity<GlobalResponse> deleteHandymanRating(@RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok().body(providerService.deleteHandymanRating(body.get("id")));
    }
}
