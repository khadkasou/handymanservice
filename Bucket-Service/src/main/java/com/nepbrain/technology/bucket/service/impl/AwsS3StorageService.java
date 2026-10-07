package com.nepbrain.technology.bucket.service.impl;

import com.nepbrain.technology.bucket.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsS3StorageService implements StorageService {

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    private final S3Client s3Client;

    @Override
    public String uploadFile(byte[] data, String contentType, String objectPath) throws Exception {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectPath)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
            log.debug("✅ File uploaded to AWS S3: {}", objectPath);
            return objectPath;
        } catch (Exception e) {
            log.error("❌ Failed to upload to AWS S3: {}", e.getMessage());
            throw new Exception("Error uploading file to AWS S3: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream retrieveFile(String objectPath) throws Exception {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectPath)
                    .build();

            return s3Client.getObjectAsBytes(getObjectRequest).asInputStream();
        } catch (NoSuchKeyException e) {
            log.error("❌ File not found in AWS S3: {}", objectPath);
            throw new Exception("File not found: " + objectPath, e);
        } catch (Exception e) {
            log.error("❌ Failed to retrieve file from AWS S3: {}", e.getMessage());
            throw new Exception("Error retrieving file from AWS S3: " + e.getMessage(), e);
        }
    }

    @Override
    public HttpHeaders getFileMetadata(String objectPath) throws Exception {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectPath)
                    .build();

            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            
            HttpHeaders headers = new HttpHeaders();
            String contentType = response.contentType();
            if (contentType != null) {
                headers.setContentType(MediaType.parseMediaType(contentType));
            }
            if (response.contentLength() != null) {
                headers.setContentLength(response.contentLength());
            }
            return headers;
        } catch (NoSuchKeyException e) {
            log.error("❌ File not found in AWS S3: {}", objectPath);
            throw new Exception("File not found: " + objectPath, e);
        } catch (Exception e) {
            log.error("❌ Failed to get file metadata from AWS S3: {}", e.getMessage());
            throw new Exception("Error getting file metadata from AWS S3: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String objectPath) throws Exception {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectPath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.debug("✅ File deleted from AWS S3: {}", objectPath);
        } catch (Exception e) {
            log.error("❌ Failed to delete file from AWS S3: {}", e.getMessage());
            throw new Exception("Error deleting file from AWS S3: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFiles(List<String> objectPaths) throws Exception {
        try {
            List<ObjectIdentifier> objectIdentifiers = objectPaths.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .toList();

            Delete delete = Delete.builder()
                    .objects(objectIdentifiers)
                    .build();

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(delete)
                    .build();

            s3Client.deleteObjects(deleteObjectsRequest);
            log.debug("✅ {} files deleted from AWS S3", objectPaths.size());
        } catch (Exception e) {
            log.error("❌ Failed to delete files from AWS S3: {}", e.getMessage());
            throw new Exception("Error deleting files from AWS S3: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String objectPath) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectPath)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.warn("⚠️ Error checking file existence in AWS S3: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "AWS_S3";
    }
}

