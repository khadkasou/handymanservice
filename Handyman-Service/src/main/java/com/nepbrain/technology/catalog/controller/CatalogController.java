package com.nepbrain.technology.catalog.controller;

import com.nepbrain.technology.catalog.annotation.CustomCatalogRestController;
import com.nepbrain.technology.catalog.payload.request.FavouriteRequest;
import com.nepbrain.technology.catalog.payload.request.ServiceRequest;
import com.nepbrain.technology.catalog.service.CatalogService;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@CustomCatalogRestController
@RequiredArgsConstructor
@Tag(name = "Catalog")
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/category-list")
    @Operation(summary = "Category list")
    public ResponseEntity<GlobalResponse> categoryList() {
        return ResponseEntity.ok().body(catalogService.categoryList());
    }

    @GetMapping("/subcategory-list")
    @Operation(summary = "Subcategory list")
    public ResponseEntity<GlobalResponse> subcategoryList(@RequestParam("category_id") UUID categoryId) {
        return ResponseEntity.ok().body(catalogService.subcategoryList(categoryId));
    }

    @GetMapping("/service-list")
    @Operation(summary = "Service list")
    public ResponseEntity<GlobalResponse> serviceList(@RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok().body(catalogService.serviceList(customerId));
    }

    @PostMapping("/service-detail")
    @Operation(summary = "Service detail")
    public ResponseEntity<GlobalResponse> serviceDetail(@RequestBody ServiceRequest request) {
        return ResponseEntity.ok().body(catalogService.serviceDetail(request));
    }

    @GetMapping("/search-list")
    @Operation(summary = "Search services")
    public ResponseEntity<GlobalResponse> searchList(@RequestParam(required = false) String search,
                                                     @RequestParam(value = "category_id", required = false) UUID categoryId) {
        return ResponseEntity.ok().body(catalogService.searchList(search, categoryId));
    }

    @PostMapping("/save-favourite")
    @Operation(summary = "Save favourite service")
    public ResponseEntity<GlobalResponse> saveFavourite(@RequestBody FavouriteRequest request) {
        return ResponseEntity.ok().body(catalogService.saveFavourite(request));
    }

    @PutMapping("/favourite/status")
    @Operation(summary = "Change favourite service status")
    public ResponseEntity<GlobalResponse> changeFavouriteStatus(@RequestBody FavouriteRequest request,
                                                                @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(catalogService.changeFavouriteStatus(request, status));
    }

    @PostMapping("/delete-favourite")
    @Operation(summary = "Mark favourite service inactive")
    public ResponseEntity<GlobalResponse> deleteFavourite(@RequestBody FavouriteRequest request) {
        return ResponseEntity.ok().body(catalogService.deleteFavourite(request));
    }

    @GetMapping("/user-favourite-service")
    @Operation(summary = "User favourite services")
    public ResponseEntity<GlobalResponse> userFavouriteService(@RequestParam(value = "user_id", required = false) UUID userId) {
        return ResponseEntity.ok().body(catalogService.userFavouriteService(userId));
    }
}
