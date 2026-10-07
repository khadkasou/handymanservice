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

    @Value("${mail.aws-access-key}")
    private String awsAccessKey;

    @Value("${mail.aws-secret-key}")
    private String awsSecretKey;

    @Value("${mail.aws-region}")
    private String awsRegion;

    @Bean
    public SesV2Client sesV2Client() {
        return SesV2Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .build();
    }
}
