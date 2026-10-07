package com.nepbrain.technology.bucket.config;

import com.nepbrain.technology.bucket.enums.StorageType;
import com.nepbrain.technology.bucket.service.StorageService;
import com.nepbrain.technology.bucket.service.impl.AwsS3StorageService;
import com.nepbrain.technology.bucket.service.impl.MinioStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@Slf4j
public class AwsConfig {

    @Value("${storage.type:MINIO}")
    private String storageType;

    @Value("${aws.s3.accessKey:}")
    private String awsAccessKey;

    @Value("${aws.s3.secretKey:}")
    private String awsSecretKey;

    @Value("${aws.s3.region:us-east-1}")
    private String awsRegion;

    @Value("${aws.s3.endpoint:}")
    private String awsEndpoint;

    @Bean
    @Primary
    public StorageService storageService(MinioStorageService minioStorageService,
                                         AwsS3StorageService awsS3StorageService) {
        StorageType type = StorageType.fromString(storageType);
        
        switch (type) {
            case AWS_S3:
                log.info("✅ Using AWS S3 as storage service");
                return awsS3StorageService;
            case MINIO:
            default:
                log.info("✅ Using MinIO as storage service");
                return minioStorageService;
        }
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        
        var builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(awsRegion));

        // If custom endpoint is provided (e.g., for S3-compatible services), use it
        if (awsEndpoint != null && !awsEndpoint.isEmpty()) {
            builder.endpointOverride(URI.create(awsEndpoint));
            log.info("✅ AWS S3 client configured with custom endpoint: {}", awsEndpoint);
        } else {
            log.info("✅ AWS S3 client configured for region: {}", awsRegion);
        }

        return builder.build();
    }
}

