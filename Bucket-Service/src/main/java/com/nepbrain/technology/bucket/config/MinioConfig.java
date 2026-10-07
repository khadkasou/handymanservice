package com.nepbrain.technology.bucket.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucket-name:hrmsuite-bucket}")
    private String defaultBucket;

    @Value("${minio.secure:true}")
    private boolean secureConnection;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            ensureBucketExists(client, defaultBucket);
            log.info("✅ Connected to MinIO: {} | Bucket ensured: {}", endpoint, defaultBucket);
            return client;
        } catch (Exception e) {
            log.error("❌ Failed to initialize MinIO client: {}", e.getMessage(), e);
            throw new IllegalStateException("MinIO initialization failed", e);
        }
    }

    /**
     * Create the bucket automatically if it doesn’t exist.
     */
    private void ensureBucketExists(MinioClient client, String bucketName) {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("🪣 Created new MinIO bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("⚠️ Error while ensuring bucket {}: {}", bucketName, e.getMessage(), e);
            throw new IllegalStateException("Failed to verify or create bucket: " + bucketName, e);
        }
    }
}
