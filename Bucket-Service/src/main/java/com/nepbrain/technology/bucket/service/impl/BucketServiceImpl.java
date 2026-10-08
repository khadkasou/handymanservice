package com.nepbrain.technology.bucket.service.impl;

import com.nepbrain.technology.bucket.payload.request.BucketRequest;
import com.nepbrain.technology.bucket.payload.request.DocRequest;
import com.nepbrain.technology.bucket.payload.request.DocumentRequest;
import com.nepbrain.technology.bucket.payload.response.BucketResponse;
import com.nepbrain.technology.bucket.payload.response.DocumentResponse;
import com.nepbrain.technology.bucket.service.BucketService;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.util.Constant;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.bucket.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.nepbrain.technology.bucket.util.ContentTypeFromBase64.getFileExtensionFromContentType;
import static com.nepbrain.technology.bucket.util.ContentTypeFromBase64.identifyContentType;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketServiceImpl implements BucketService {

    @Value("${minio.bucketName:${aws.s3.bucketName:erp}}")
    private String bucketName;

    @Value("${minio.retrieveUrl}")
    private String bucketPath;

    @Value("${server.url}")
    private String serverUrl;

    private static final String FOLDER_SEPARATOR = "/";
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final StorageService storageService;
    private final ModelMapper modelMapper = new ModelMapper();

    // ========================================
    // Upload Single File
    // ========================================
    @Override
    public GlobalResponse uploadImage(ServerHttpRequest request, BucketRequest bucketRequest) throws GlobalException {
        String url = uploadImageAndGetUrl(bucketRequest);
        return GlobalResponseBuilder.buildSuccessResponse("message.bucket.upload.success", url);
    }

    @Override
    public String uploadImageAndGetUrl(BucketRequest bucketRequest) throws GlobalException {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(bucketRequest.getEncodedData().getBytes(StandardCharsets.UTF_8));
            if (decodedBytes == null || decodedBytes.length == 0) {
                throw new GlobalException("BUK001", "Decoded file content is empty.");
            }
            String contentType = identifyContentType(decodedBytes);
            String extension = getFileExtensionFromContentType(contentType);
            if (extension == null) {
                throw new GlobalException("BUK012", "Unsupported or invalid file format");
            }

            String baseName = bucketRequest.getFileName() != null
                    ? bucketRequest.getFileName().replaceAll("\\.[^.]+$", "")
                    : "document";
            String fileName = sanitizeFileName(baseName) + "_" + LocalDateTime.now().format(FILE_TIMESTAMP);
            String objectPath = buildObjectPath(bucketRequest.getFolderPath(), fileName, extension);
            log.debug("📤 Uploading file: objectPath={}, contentType={}, size={} bytes", objectPath, contentType, decodedBytes.length);

            String uploadedPath = storageService.uploadFile(decodedBytes, contentType, objectPath);
            if (!storageService.fileExists(uploadedPath)) {
                log.error("❌ Upload reported success but file does not exist: {}", uploadedPath);
                throw new GlobalException("BUK001", "File upload verification failed.");
            }
            String url = serverUrl + bucketPath + FOLDER_SEPARATOR + uploadedPath;
            log.info("✅ File '{}' uploaded successfully to {} ({}). URL: {}", fileName, bucketName, storageService.getStorageType(), url);
            return url;
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error uploading image: {}", e.getMessage(), e);
            throw new GlobalException("BUK001", "Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public String uploadMultipartAndGetUrl(byte[] data, String contentType, String fileName, String folderPath) throws GlobalException {
        try {
            if (data == null || data.length == 0) {
                throw new GlobalException("BUK001", "File data is required");
            }
            String type = contentType != null ? contentType.split(";")[0].trim() : "application/octet-stream";
            String extension = getFileExtensionFromContentType(type);
            if (extension == null && fileName != null && fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
            }
            String safeName = sanitizeFileName(fileName != null ? fileName.replaceAll("\\.[^.]+$", "") : "document")
                    + "_" + LocalDateTime.now().format(FILE_TIMESTAMP);
            String objectPath = buildObjectPath(folderPath != null ? folderPath : "uploads", safeName, extension);
            String uploadedPath = storageService.uploadFile(data, type, objectPath);
            if (!storageService.fileExists(uploadedPath)) {
                log.error("❌ Multipart upload reported success but file does not exist: {}", uploadedPath);
                throw new GlobalException("BUK001", "File upload verification failed.");
            }
            String url = serverUrl + bucketPath + FOLDER_SEPARATOR + uploadedPath;
            log.info("✅ Multipart file '{}' uploaded successfully. URL: {}", safeName, url);
            return url;
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error uploading multipart file: {}", e.getMessage(), e);
            throw new GlobalException("BUK001", "Failed to upload file: " + e.getMessage());
        }
    }

    // ========================================
    // Bulk Upload Files
    // ========================================
    @Override
    public List<DocumentResponse> uploadFileInBulk(ServerHttpRequest request, List<DocumentRequest> documentRequests) throws GlobalException {
        log.info("📦 Bulk file upload started. Total files: {}", documentRequests.size());
        List<String> uploadedFilePaths = new ArrayList<>();
        List<DocRequest> preparedDocs = new ArrayList<>();

        // Step 1: Validate all files before upload
        for (DocumentRequest document : documentRequests) {
            byte[] decodedBytes = Base64.getDecoder().decode(document.getEncodedData().getBytes(StandardCharsets.UTF_8));
            String contentType = identifyContentType(decodedBytes);
            String extension = getFileExtensionFromContentType(contentType);

            if (!isValidExtension(extension)) {
                throw new GlobalException("BUK002", "Invalid file type for file: " + document.getFileName());
            }

            DocRequest doc = modelMapper.map(document, DocRequest.class);
            doc.setContentType(contentType);
            doc.setExtension(extension);
            doc.setDecodedBytes(decodedBytes);
            preparedDocs.add(doc);
        }

        // Step 2: Upload validated files
        try {
            List<DocumentResponse> responses = new ArrayList<>();
            for (DocRequest doc : preparedDocs) {
                String fileName = sanitizeFileName(doc.getFileName()) + "_" + System.currentTimeMillis();
                if (doc.getExtension() != null) fileName += "." + doc.getExtension();
                String objectPath = doc.getModule() + FOLDER_SEPARATOR + doc.getType() + FOLDER_SEPARATOR + fileName;

                String uploadedPath = storageService.uploadFile(doc.getDecodedBytes(), doc.getContentType(), objectPath);
                String fileUrl = serverUrl + bucketPath + FOLDER_SEPARATOR + uploadedPath;
                responses.add(DocumentResponse.builder()
                        .filePath(fileUrl)
                        .type(doc.getType())
                        .fileName(fileName)
                        .build());
                uploadedFilePaths.add(objectPath);
            }

            log.info("✅ Bulk upload completed successfully. Total uploaded: {}", responses.size());
            return responses;

        } catch (Exception e) {
            log.error("❌ Bulk upload failed: {}. Rolling back uploaded files...", e.getMessage());
            rollbackUploadedFiles(uploadedFilePaths);
            throw new GlobalException("BUK003", "Bulk upload failed, all uploaded files rolled back.");
        }
    }

    // ========================================
    // Retrieve Image
    // ========================================
    @Override
    public BucketResponse retrieveImage(UUID userId, ServerHttpRequest request) throws GlobalException {
        if (userId != null && !userId.toString().equals(getUserIdFromRequest(request))) {
            log.error("⚠️ Unauthorized access attempt for user: {}", userId);
            throw new GlobalException("AUTH001", "Unauthorized access to document.");
        }
        return viewDocument(request);
    }

    private BucketResponse viewDocument(ServerHttpRequest request) throws GlobalException {
        String fullPath = request.getURI().getPath();
        
        // Extract object name from URL path
        // URL format: /api/v2/bucket/public/retrieve-bucket/{objectPath}
        // We need to remove the bucketPath prefix
        String objectName;
        if (fullPath.startsWith(bucketPath)) {
            objectName = fullPath.substring(bucketPath.length());
            // Remove leading slash if present
            if (objectName.startsWith("/")) {
                objectName = objectName.substring(1);
            }
        } else {
            // Fallback: try to extract path after last occurrence of retrieve-bucket
            int lastIndex = fullPath.lastIndexOf("retrieve-bucket");
            if (lastIndex >= 0) {
                objectName = fullPath.substring(lastIndex + "retrieve-bucket".length());
                if (objectName.startsWith("/")) {
                    objectName = objectName.substring(1);
                }
            } else {
                objectName = fullPath.replaceFirst("^/", "");
            }
        }
        
        log.info("📥 Retrieving document: fullPath={}, bucketPath={}, extractedObjectName={}", 
                fullPath, bucketPath, objectName);
        
        try {
            // Try to retrieve directly - fileExists might have timing issues
            // If file doesn't exist, MinIO will throw an exception which we'll catch
            log.debug("✅ Attempting to retrieve file: {}", objectName);
            HttpHeaders headers = storageService.getFileMetadata(objectName);
            InputStream imageStream = storageService.retrieveFile(objectName);

            log.info("✅ Successfully retrieved file: {}", objectName);
            return BucketResponse.builder()
                    .headers(headers)
                    .resource(new InputStreamResource(imageStream))
                    .build();

        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error retrieving document: objectName={}, fullPath={}, error={}", 
                    objectName, fullPath, e.getMessage(), e);
            throw new GlobalException("BUK004", "Document not found: " + objectName);
        }
    }

    // ========================================
    // Helper Methods
    // ========================================
    private void rollbackUploadedFiles(List<String> uploadedFilePaths) {
        try {
            if (!uploadedFilePaths.isEmpty()) {
                storageService.deleteFiles(uploadedFilePaths);
                log.info("♻️ Rollback successful. {} files removed from {}.", uploadedFilePaths.size(), storageService.getStorageType());
            }
        } catch (Exception ex) {
            log.warn("⚠️ Rollback partially failed: {}", ex.getMessage());
        }
    }

    private boolean isValidExtension(String extension) {
        return List.of("jpg", "jpeg", "png", "pdf", "docx").contains(extension.toLowerCase(Locale.ROOT));
    }

    /**
     * Reactive method to get userId from security context.
     * This method is non-blocking and works in reactive Spring Boot applications.
     * Extracts userId from opaque token introspection response (sub claim).
     * 
     * @return Mono containing the userId (sub claim) or empty Mono if not found
     */
    public Mono<String> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .cast(SecurityContext.class)
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication != null && authentication.isAuthenticated())
                .cast(BearerTokenAuthentication.class)
                .map(BearerTokenAuthentication::getPrincipal)
                .cast(OAuth2AuthenticatedPrincipal.class)
                .map(principal -> {
                    // Try userId attribute first (set by CustomOpaqueTokenIntrospector)
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
                .filter(userId -> userId != null && !userId.isEmpty() && !userId.equalsIgnoreCase("anonymousUser"))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("⚠️ No userId found in opaque token principal attributes");
                    return Mono.empty();
                }))
                .doOnNext(userId -> log.debug("✅ Extracted userId from opaque token: {}", userId))
                .doOnError(error -> log.error("⚠️ Error extracting userId from reactive security context: {}", error.getMessage(), error));
    }

    /**
     * Extracts userId from Authentication object.
     * Tries multiple sources: principal name, userId attribute, sub attribute.
     * 
     * @param authentication The authentication object
     * @return userId string or null if not found
     */
    private String extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        
        // First, try to get from principal name (set by CustomOpaqueTokenIntrospector)
        String userId = authentication.getName();
        if (userId != null && !userId.isEmpty() && !userId.equalsIgnoreCase("anonymousUser")) {
            return userId;
        }
        
        // Fallback: Try to get from OAuth2AuthenticatedPrincipal attributes
        if (authentication instanceof BearerTokenAuthentication bearerAuth) {
            Object principalObj = bearerAuth.getPrincipal();
            if (principalObj instanceof OAuth2AuthenticatedPrincipal principal) {
                // Try userId attribute first
                Object userIdAttr = principal.getAttribute("userId");
                if (userIdAttr != null) {
                    return userIdAttr.toString();
                }
                // Try sub attribute (from introspection response)
                Object subAttr = principal.getAttribute("sub");
                if (subAttr != null) {
                    return subAttr.toString();
                }
                // Use principal name as fallback
                String principalName = principal.getName();
                if (principalName != null && !principalName.isEmpty() && !principalName.equalsIgnoreCase("anonymousUser")) {
                    return principalName;
                }
            }
        }
        
        return null;
    }

    private String getUserIdFromRequest(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(Constant.AUTHORIZATION);
        if (ObjectUtils.isEmpty(authorization)) return null;
        return extractUserIdFromToken(authorization);
    }

    private String extractUserIdFromToken(String token) {
        try {
            Authentication authentication = null;

            // Try SecurityContextHolder first (works in blocking contexts)
            try {

                authentication = SecurityContextHolder.getContext().getAuthentication();
            } catch (Exception e) {
                // If SecurityContextHolder fails (e.g., on reactive thread), 
                // we'll return null and log a warning
                log.debug("SecurityContextHolder not available, may be on reactive thread: {}", e.getMessage());
            }
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("⚠️ No authenticated user found in security context");
                return null;
            }
            
            // Use the shared extraction method
            String userId = extractUserIdFromAuthentication(authentication);
            if (userId != null) {
                return userId;
            }
            
            log.warn("⚠️ Could not extract userId from authentication");
            return null;
        } catch (IllegalStateException e) {
            // Handle "block()/blockFirst()/blockLast() are blocking" error
            if (e.getMessage() != null && e.getMessage().contains("blocking")) {
                log.warn("⚠️ Cannot access security context on reactive thread. Consider using reactive approach or ensuring blocking thread context.");
            }
            return null;
        } catch (Exception e) {
            log.warn("⚠️ Failed to extract userId from token: {}", e.getMessage());
            return null;
        }
    }

    private String buildObjectPath(String folderPath, String fileName, String extension) {
        StringBuilder sb = new StringBuilder(folderPath)
                .append(FOLDER_SEPARATOR)
                .append(fileName);
        if (extension != null) sb.append(".").append(extension);
        return sb.toString();
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9-_]", "_").toLowerCase(Locale.ROOT);
    }
}
