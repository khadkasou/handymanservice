package com.nepbrain.technology.auth.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.auth.payload.response.UserServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceCommunication {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${service.url.user:http://localhost:8001/api/v2/account/}")
    private String userServiceUrl;

    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.secret}")
    private String secret;

    /**
     * Get user by email from User Service
     *
     * @param email User email address
     * @return UserServiceResponse containing user information
     * @throws RuntimeException if user not found or service error
     */
    public UserServiceResponse getUserByEmail(String email) {
        try {
            String authHeader = createBasicAuthHeader(clientId, secret);

            Map<String, Object> response = webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "user/get-by-email")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .header("email", email)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        if (clientResponse.statusCode().value() == 404) {
                            return clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("User not found: " + email));
                        }
                        return clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("User service error: " + body));
                    })
                    .bodyToMono(Map.class)
                    .timeout(java.time.Duration.ofSeconds(8))
                    .block();

            if (response == null || response.get("data") == null) {
                throw new RuntimeException("User not found: " + email);
            }

            // Convert response data to UserServiceResponse
            Object data = response.get("data");
            return objectMapper.convertValue(data, UserServiceResponse.class);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new RuntimeException("User not found: " + email);
            }
            throw new RuntimeException("Error calling User service: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing User service response: " + e.getMessage(), e);
        }
    }

    /**
     * Get user by ID from User Service
     *
     * @param userId User ID
     * @return UserServiceResponse containing user information
     * @throws RuntimeException if user not found or service error
     */
    public UserServiceResponse getUserById(UUID userId) {
        try {
            String authHeader = createBasicAuthHeader(clientId, secret);

            Map<String, Object> response = webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "user/get-by-id")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .header("id", userId.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        if (clientResponse.statusCode().value() == 404) {
                            return clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("User not found: " + userId));
                        }
                        return clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("User service error: " + body));
                    })
                    .bodyToMono(Map.class)
                    .timeout(java.time.Duration.ofSeconds(8))
                    .block();

            if (response == null || response.get("data") == null) {
                throw new RuntimeException("User not found: " + userId);
            }

            Object data = response.get("data");
            return objectMapper.convertValue(data, UserServiceResponse.class);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new RuntimeException("User not found: " + userId);
            }
            throw new RuntimeException("Error calling User service: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing User service response: " + e.getMessage(), e);
        }
    }

    private String createBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        byte[] encodedBytes = Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}

