package com.nepbrain.technology.communication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

/**
 * Email: AWS SES only. No Outlook/SMTP.
 */
@Configuration
public class EmailConfig {

    @Value("${mail.aws-access-key:}")
    private String awsAccessKey;

    @Value("${mail.aws-secret-key:}")
    private String awsSecretKey;

    @Value("${mail.aws-region:us-east-1}")
    private String awsRegion;

    @Bean
    public SesV2Client sesV2Client() {
        String access = awsAccessKey != null ? awsAccessKey : "";
        String secret = awsSecretKey != null ? awsSecretKey : "";
        return SesV2Client.builder()
                .region(Region.of(awsRegion != null && !awsRegion.isBlank() ? awsRegion : "us-east-1"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        access.isBlank() ? "missing" : access,
                                        secret.isBlank() ? "missing" : secret)))
                .build();
    }
}
