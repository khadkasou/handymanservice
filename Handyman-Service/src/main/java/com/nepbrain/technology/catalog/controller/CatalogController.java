package com.nepbrain.technology.catalog.controller;
import com.nepbrain.technology.catalog.annotation.CustomCatalogRestController;
import com.nepbrain.technology.catalog.payload.request.*;
import com.nepbrain.technology.catalog.service.CatalogService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@CustomCatalogRestController @RequiredArgsConstructor
@Tag(name = "Catalog", description = "Category and service APIs")
public class CatalogController {
    private final CatalogService catalogService;
    @GetMapping("/category-list") public ResponseEntity<GlobalResponse> categoryList() {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CATEGORY_LIST", catalogService.categoryList()));
    }
    @GetMapping("/subcategory-list") public ResponseEntity<GlobalResponse> subcategoryList(@RequestParam("category_id") UUID categoryId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SUBCATEGORY_LIST", catalogService.subcategoryList(categoryId)));
    }
    @GetMapping("/service-list") public ResponseEntity<GlobalResponse> serviceList(@RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SERVICE_LIST", catalogService.serviceList(customerId)));
    }
    @PostMapping("/service-detail")
    public ResponseEntity<GlobalResponse> serviceDetail(@RequestBody ServiceRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SERVICE_DETAIL", catalogService.serviceDetail(request)));
    }
    @GetMapping("/search-list") public ResponseEntity<GlobalResponse> searchList(@RequestParam(required = false) String search, @RequestParam(value = "category_id", required = false) UUID categoryId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SEARCH_LIST", catalogService.searchList(search, categoryId)));
    }
    @PostMapping("/save-favourite") public ResponseEntity<GlobalResponse> saveFavourite(@RequestBody FavouriteRequest request) {
        catalogService.saveFavourite(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SAVE_FAVOURITE", "Favourite saved"));
    }
    @PostMapping("/delete-favourite") public ResponseEntity<GlobalResponse> deleteFavourite(@RequestBody FavouriteRequest request) {
        catalogService.deleteFavourite(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DELETE_FAVOURITE", "Favourite deleted"));
    }
    @GetMapping("/user-favourite-service") public ResponseEntity<GlobalResponse> userFavouriteService(@RequestParam(value = "user_id", required = false) UUID userId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("USER_FAVOURITE_SERVICE", catalogService.userFavouriteService(userId)));
    }
}
