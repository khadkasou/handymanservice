package com.nepbrain.technology.provider.controller;
import com.nepbrain.technology.provider.annotation.CustomProviderRestController;
import com.nepbrain.technology.provider.payload.request.*;
import com.nepbrain.technology.provider.service.ProviderService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.Map; import java.util.UUID;

@CustomProviderRestController @RequiredArgsConstructor
@Tag(name = "Provider", description = "Provider and handyman APIs")
public class ProviderController {
    private final ProviderService providerService;
    @GetMapping("/user-list") public ResponseEntity<GlobalResponse> userList(@RequestParam("user_type") String userType) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("USER_LIST", providerService.userList(userType)));
    }
    @GetMapping("/user-detail") public ResponseEntity<GlobalResponse> userDetail(@RequestParam UUID id) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("USER_DETAIL", providerService.providerDetail(id)));
    }
    @PostMapping("/save-favourite-provider") public ResponseEntity<GlobalResponse> saveFavouriteProvider(@RequestBody FavouriteProviderRequest request) {
        providerService.saveFavouriteProvider(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SAVE_FAVOURITE_PROVIDER", "Favourite provider saved"));
    }
    @PostMapping("/delete-favourite-provider") public ResponseEntity<GlobalResponse> deleteFavouriteProvider(@RequestBody FavouriteProviderRequest request) {
        providerService.deleteFavouriteProvider(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DELETE_FAVOURITE_PROVIDER", "Favourite provider deleted"));
    }
    @GetMapping("/user-favourite-provider") public ResponseEntity<GlobalResponse> userFavouriteProvider(@RequestParam(value = "user_id", required = false) UUID userId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("USER_FAVOURITE_PROVIDER", providerService.userFavouriteProvider(userId)));
    }
    @PostMapping("/save-handyman-rating") public ResponseEntity<GlobalResponse> saveHandymanRating(@RequestBody HandymanRatingRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SAVE_HANDYMAN_RATING", providerService.saveHandymanRating(request)));
    }
    @PostMapping("/handyman-reviews") public ResponseEntity<GlobalResponse> handymanReviews(@RequestBody HandymanRatingRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("HANDYMAN_REVIEWS", providerService.handymanReviews(request.getHandymanId())));
    }
    @PostMapping("/delete-handyman-rating") public ResponseEntity<GlobalResponse> deleteHandymanRating(@RequestBody Map<String, UUID> body) {
        providerService.deleteHandymanRating(body.get("id"));
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DELETE_HANDYMAN_RATING", "Rating deleted"));
    }
}
