package com.nepbrain.technology.bucket.service;

import com.nepbrain.technology.bucket.payload.request.BucketRequest;
import com.nepbrain.technology.bucket.payload.request.DocumentRequest;
import com.nepbrain.technology.bucket.payload.response.BucketResponse;
import com.nepbrain.technology.bucket.payload.response.DocumentResponse;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface BucketService {

    GlobalResponse uploadImage(ServerHttpRequest request, BucketRequest bucketRequest) throws GlobalException;

    /**
     * Upload base64 image and return the file URL only. Used by gRPC and other services.
     */
    String uploadImageAndGetUrl(BucketRequest bucketRequest) throws GlobalException;

    /**
     * Upload multipart file bytes to storage and return the file URL (same style as base64 upload).
     * Stores bytes as-is (no watermark).
     */
    String uploadMultipartAndGetUrl(byte[] data, String contentType, String fileName, String folderPath) throws GlobalException;

    List<DocumentResponse> uploadFileInBulk(ServerHttpRequest request, List<DocumentRequest> documentRequests) throws GlobalException;

    BucketResponse retrieveImage(UUID userId, ServerHttpRequest request) throws GlobalException;

    /**
     * Get current authenticated user ID from opaque token.
     * This method extracts userId from the reactive security context.
     * 
     * @return Mono containing the userId (sub claim) or empty Mono if not found
     */
    Mono<String> getUserId();
}
