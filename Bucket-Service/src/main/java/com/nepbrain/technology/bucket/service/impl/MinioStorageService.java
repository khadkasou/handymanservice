package com.nepbrain.technology.bucket.service.impl;

import com.nepbrain.technology.bucket.service.StorageService;
import io.minio.*;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    @Value("${minio.bucketName}")
    private String bucketName;

    private final MinioClient minioClient;

    @Override
    public String uploadFile(byte[] data, String contentType, String objectPath) throws Exception {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("File data cannot be null or empty");
        }
        
        // Normalize object path (remove leading slashes, keep trailing structure)
        // MinIO expects paths without leading slash
        String normalizedPath = objectPath.replaceAll("^/+", "").replaceAll("/+", "/");
        
        log.debug("📤 MinIO upload: originalPath={}, normalizedPath={}, size={} bytes", 
                objectPath, normalizedPath, data.length);
        
        try (java.io.InputStream inputStream = new java.io.ByteArrayInputStream(data)) {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(normalizedPath)
                            .contentType(contentType)
                            .stream(inputStream, data.length, -1)
                            .build());
            
            String uploadedObjectPath = response.object();
            
            // Ensure we return the normalized path (response.object() should match, but verify)
            if (!uploadedObjectPath.equals(normalizedPath)) {
                log.warn("⚠️ Path mismatch: requested={}, returned={}, using normalized", 
                        normalizedPath, uploadedObjectPath);
                uploadedObjectPath = normalizedPath;
            }
            
            log.info("✅ File uploaded to MinIO: bucket={}, object={}, size={} bytes", 
                    bucketName, uploadedObjectPath, data.length);
            
            // Verify the upload by checking if object exists (with retry for eventual consistency)
            boolean verified = false;
            for (int i = 0; i < 3; i++) {
                try {
                    minioClient.statObject(
                            StatObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(uploadedObjectPath)
                                    .build());
                    verified = true;
                    log.debug("✅ Upload verification successful for: {}", uploadedObjectPath);
                    break;
                } catch (Exception verifyEx) {
                    if (i < 2) {
                        log.debug("⏳ Upload verification attempt {} failed, retrying...", i + 1);
                        Thread.sleep(100); // Brief delay for eventual consistency
                    } else {
                        log.warn("⚠️ Upload reported success but verification failed after 3 attempts: {}", uploadedObjectPath);
                    }
                }
            }
            
            return uploadedObjectPath;
        } catch (Exception e) {
            log.error("❌ Failed to upload to MinIO: path={}, error={}", normalizedPath, e.getMessage(), e);
            throw new Exception("Error uploading file to MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream retrieveFile(String objectPath) throws Exception {
        // Normalize path to match upload normalization
        String normalizedPath = objectPath.replaceAll("^/+", "").replaceAll("/+", "/");
        
        log.debug("📥 Retrieving file from MinIO: originalPath={}, normalizedPath={}", 
                objectPath, normalizedPath);
        
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(normalizedPath)
                            .build());
        } catch (Exception e) {
            log.error("❌ Failed to retrieve file from MinIO: path={}, error={}", normalizedPath, e.getMessage(), e);
            throw new Exception("Error retrieving file from MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public HttpHeaders getFileMetadata(String objectPath) throws Exception {
        // Normalize path to match upload normalization
        String normalizedPath = objectPath.replaceAll("^/+", "").replaceAll("/+", "/");
        
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(normalizedPath)
                            .build());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(stat.contentType()));
            headers.setContentLength(stat.size());
            return headers;
        } catch (Exception e) {
            log.error("❌ Failed to get file metadata from MinIO: path={}, error={}", normalizedPath, e.getMessage(), e);
            throw new Exception("Error getting file metadata from MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String objectPath) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build());
            log.debug("✅ File deleted from MinIO: {}", objectPath);
        } catch (Exception e) {
            log.error("❌ Failed to delete file from MinIO: {}", e.getMessage());
            throw new Exception("Error deleting file from MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFiles(List<String> objectPaths) throws Exception {
        try {
            List<DeleteObject> objects = objectPaths.stream()
                    .map(DeleteObject::new)
                    .toList();
            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build());
            log.debug("✅ {} files deleted from MinIO", objectPaths.size());
        } catch (Exception e) {
            log.error("❌ Failed to delete files from MinIO: {}", e.getMessage());
            throw new Exception("Error deleting files from MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String objectPath) {
        // Normalize path to match upload normalization
        String normalizedPath = objectPath.replaceAll("^/+", "").replaceAll("/+", "/");
        
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(normalizedPath)
                            .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "MINIO";
    }
}

