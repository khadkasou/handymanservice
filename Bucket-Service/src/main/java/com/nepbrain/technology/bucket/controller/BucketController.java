package com.nepbrain.technology.bucket.controller;

import com.nepbrain.technology.bucket.annotation.BucketRestController;
import com.nepbrain.technology.bucket.payload.request.BucketRequest;
import com.nepbrain.technology.bucket.payload.request.DocumentRequest;
import com.nepbrain.technology.bucket.payload.response.BucketResponse;
import com.nepbrain.technology.bucket.service.BucketService;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@BucketRestController
@RequiredArgsConstructor
@Tag(name = "Bucket")
@Slf4j
public class BucketController {

    private final BucketService bucketService;

    @Operation(summary = "Upload file to minio server")
    @ApiResponse(responseCode = "500", description = "Failed Operation")
    @PostMapping("/upload")
    public ResponseEntity<GlobalResponse> uploadImage(@Valid @RequestBody BucketRequest bucketRequest, ServerHttpRequest request) throws GlobalException {
        log.info("Uploading file to bucket service");
        return ResponseEntity.ok()
                .body(bucketService.uploadImage(request, bucketRequest));
    }

    @Operation(summary = "Upload file to minio server")
    @ApiResponse(responseCode = "500", description = "Failed Operation")
    @PostMapping("/upload/micro")
    public ResponseEntity<GlobalResponse> uploadMicroImage(@Valid @RequestBody BucketRequest bucketRequest, ServerHttpRequest request) throws GlobalException {
        log.info("Uploading file to bucket service");
        return ResponseEntity.ok()
                .body(GlobalResponseBuilder.buildSuccessResponse("File uploaded successfully", bucketService.uploadImage(request, bucketRequest)));
    }

    @Operation(summary = "Upload bulk file to minio server")
    @ApiResponse(responseCode = "500", description = "Failed Operation")
    @PostMapping("/upload/bulk")
    public ResponseEntity<GlobalResponse> uploadFileBulk(@Valid @RequestBody List<DocumentRequest> documentRequests, ServerHttpRequest request) throws GlobalException {
        log.info("Uploading bulk file to bucket service");
        return ResponseEntity.ok()
                .body(GlobalResponseBuilder.buildSuccessResponse("Files uploaded successfully", bucketService.uploadFileInBulk(request, documentRequests)));
    }

    @Operation(summary = "Upload bulk file to minio server")
    @ApiResponse(responseCode = "500", description = "Failed Operation")
    @PostMapping("/upload/bulk/micro")
    public ResponseEntity<GlobalResponse> uploadMicroFileInBulk(@Valid @RequestBody List<DocumentRequest> documentRequests, ServerHttpRequest request) throws GlobalException {
        log.info("Uploading bulk file to bucket service from microservice");
        return ResponseEntity.ok()
                .body(GlobalResponseBuilder.buildSuccessResponse("Files uploaded successfully", bucketService.uploadFileInBulk(request, documentRequests)));
    }

    @Operation(summary = "Upload file via multipart/form-data; returns URL (like base64 upload). Use for non-image documents.")
    @ApiResponse(responseCode = "200", description = "File uploaded, URL in data")
    @ApiResponse(responseCode = "500", description = "Failed Operation")
    @PostMapping(value = "/upload/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<GlobalResponse>> uploadMultipart(
            @RequestPart("file") FilePart file,
            @RequestParam(value = "folderPath", required = false) String folderPath) {
        return DataBufferUtils.join(file.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(bytes -> {
                    String contentType = file.headers().getContentType() != null
                            ? Objects.requireNonNull(file.headers().getContentType()).toString().split(";")[0].trim()
                            : "application/octet-stream";
                    file.filename();
                    String fileName = file.filename();
                    String url = bucketService.uploadMultipartAndGetUrl(bytes, contentType, fileName, folderPath);
                    return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("File uploaded successfully", url));
                })
                .onErrorResume(GlobalException.class, e -> Mono.just(ResponseEntity.badRequest()
                        .body(GlobalResponseBuilder.buildFailResponse(e))))
                .doOnSuccess(r -> log.info("Multipart upload completed"));
    }

    @GetMapping("/private/{userId}/retrieve-bucket/**")
    public ResponseEntity<InputStreamResource> retrieveImage(@PathVariable UUID userId, ServerHttpRequest request) throws GlobalException {
        BucketResponse bucketResponse = bucketService.retrieveImage(userId, request);
        return ResponseEntity.ok()
                .headers(bucketResponse.getHeaders())
                .body(bucketResponse.getResource());
    }

    @GetMapping("/public/retrieve-bucket/**")
    public ResponseEntity<InputStreamResource> retrievePublicDocument(ServerHttpRequest request) throws GlobalException {
        BucketResponse bucketResponse = bucketService.retrieveImage(null, request);
        return ResponseEntity.ok()
                .headers(bucketResponse.getHeaders())
                .body(bucketResponse.getResource());
    }

    /**
     * Get current authenticated user ID from opaque token.
     * This method uses the service layer to extract userId from reactive security context.
     * 
     * @return Mono containing ResponseEntity with userId or error message
     */
    @Operation(summary = "Get current user ID")
    @ApiResponse(responseCode = "200", description = "User ID retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User ID not found")
    @GetMapping("/user/id")
    public Mono<ResponseEntity<GlobalResponse>> getCurrentUserId() {
        return bucketService.getUserId()
                .map(userId -> ResponseEntity.ok()
                        .body(GlobalResponseBuilder.buildSuccessResponse("User ID retrieved successfully", userId)))
                .switchIfEmpty(Mono.just(ResponseEntity.ok()
                        .body(GlobalResponseBuilder.buildFailResponse(
                                new GlobalException("AUTH002", "User ID not found in security context")))))
                .doOnError(error -> log.error("Error getting user ID: {}", error.getMessage()));
    }

    /**
     * Helper method to get current user ID in blocking context.
     * This can be used within blocking controller methods.
     * 
     * @return User ID string or null if not found
     */
    private String getCurrentUserIdBlocking() {
        try {
            return ReactiveSecurityContextHolder.getContext()
                    .cast(SecurityContext.class)
                    .map(SecurityContext::getAuthentication)
                    .filter(authentication -> authentication != null && authentication.isAuthenticated())
                    .cast(BearerTokenAuthentication.class)
                    .map(BearerTokenAuthentication::getPrincipal)
                    .cast(OAuth2AuthenticatedPrincipal.class)
                    .map(principal -> {
                        // Try userId attribute first
                        Object userIdAttr = principal.getAttribute("userId");
                        if (userIdAttr != null && !userIdAttr.toString().isEmpty()) {
                            return userIdAttr.toString();
                        }
                        // Try sub attribute (from introspection response)
                        Object subAttr = principal.getAttribute("sub");
                        if (subAttr != null && !subAttr.toString().isEmpty()) {
                            return subAttr.toString();
                        }
                        // Try principal name as fallback
                        String principalName = principal.getName();
                        if (principalName != null && !principalName.isEmpty() && !principalName.equalsIgnoreCase("anonymousUser")) {
                            return principalName;
                        }
                        return null;
                    })
                    .block();
        } catch (Exception e) {
            log.error("Error getting user ID in blocking context: {}", e.getMessage());
            return null;
        }
    }

    @GetMapping("/user")
    public String userDetail(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        principal.getAttributes();
        return principal.getName();
    }
}
