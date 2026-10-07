package com.nepbrain.technology.bucket.service;

import org.springframework.http.HttpHeaders;

import java.io.InputStream;
import java.util.List;

/**
 * Interface for storage service abstraction.
 * Supports both MinIO and AWS S3 implementations.
 */
public interface StorageService {
    
    /**
     * Upload a file to storage
     * @param data File data as byte array
     * @param contentType MIME type of the file
     * @param objectPath Path where the file should be stored
     * @return URL/path to access the uploaded file
     * @throws Exception if upload fails
     */
    String uploadFile(byte[] data, String contentType, String objectPath) throws Exception;
    
    /**
     * Retrieve a file from storage
     * @param objectPath Path of the file to retrieve
     * @return InputStream of the file
     * @throws Exception if retrieval fails
     */
    InputStream retrieveFile(String objectPath) throws Exception;
    
    /**
     * Get file metadata (content type, size, etc.)
     * @param objectPath Path of the file
     * @return HttpHeaders containing file metadata
     * @throws Exception if metadata retrieval fails
     */
    HttpHeaders getFileMetadata(String objectPath) throws Exception;
    
    /**
     * Delete a file from storage
     * @param objectPath Path of the file to delete
     * @throws Exception if deletion fails
     */
    void deleteFile(String objectPath) throws Exception;
    
    /**
     * Delete multiple files from storage
     * @param objectPaths List of file paths to delete
     * @throws Exception if deletion fails
     */
    void deleteFiles(List<String> objectPaths) throws Exception;
    
    /**
     * Check if a file exists
     * @param objectPath Path of the file
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String objectPath);
    
    /**
     * Get the storage type name (e.g., "MINIO", "AWS_S3")
     * @return Storage type identifier
     */
    String getStorageType();
}

