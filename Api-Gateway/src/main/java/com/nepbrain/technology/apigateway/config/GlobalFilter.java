package com.nepbrain.technology.apigateway.config;


import com.nepbrain.technology.apigateway.exception.ExceptionUtil;
import com.nepbrain.technology.apigateway.helper.AesEncryption;
import com.nepbrain.technology.apigateway.helper.Constant;
import com.nepbrain.technology.apigateway.helper.JsonMaskerUtil;
import com.nepbrain.technology.apigateway.helper.RateLimitProperties;
import com.nepbrain.technology.apigateway.payload.request.ActivityLogRequest;
import com.nepbrain.technology.apigateway.payload.response.ApiErrorResponse;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.ConnectException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalFilter implements WebFilter, Ordered {

    private final AesEncryption aesEncryption;

    @Value("${aes.xsrfToken}")
    private String xsrfToken;

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RateLimitProperties rateLimitProperties;


    private ServerHttpResponseDecorator getDecoratedResponse(ServerHttpResponse response, ActivityLogRequest activity, DataBufferFactory dataBufferFactory) {
        return new ServerHttpResponseDecorator(response) {
            @Override
            @NonNull
            public Mono<Void> writeWith(@NonNull final Publisher<? extends DataBuffer> body) {
                if (!activity.getUrl().contains("/api/v2/bucket/document/retrieve/") && body instanceof Flux<?
                        extends DataBuffer> fluxBody) {
                    return super.writeWith(fluxBody.buffer().mapNotNull(dataBuffers -> {
                        DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffers.readableByteCount()];
                        joinedBuffers.read(content);
                        String responseBody = new String(content, StandardCharsets.UTF_8);
                        if (activity.getUrl().contains("/api/")) {
                            activity.setResponseDate(new Date());
                            log.info("User: {} from IP: {} requestId: {}, method: {}, url: {}, \nResponse body :{}", activity.getUserId(), activity.getIpAddress(), activity.getRequestId(), activity.getMethod(), activity.getUrl(), responseBody);
                            activity.setResponse(responseBody);
                            activity.setStatusCode(response.getStatusCode() == null ? 200 : response.getStatusCode().value());

                            // send log based on service path
                            sendActivityLog(activity);
                        }
                        return dataBufferFactory.wrap(responseBody.getBytes());
                    })).onErrorResume(err -> {
                        log.error("Error while decorating Response: {}", err.getMessage());
                        return Mono.empty();
                    });
                }
                return super.writeWith(body);
            }
        };
    }

    private void sendActivityLog(ActivityLogRequest activity) {
        log.info(activity.getUrl());
    }

    private ServerHttpRequestDecorator getDecoratedRequest(ServerHttpRequest request, ActivityLogRequest activity) {
        final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        return new ServerHttpRequestDecorator(request) {
            @Override
            @NonNull
            public Flux<DataBuffer> getBody() {
                // Join + re-wrap so activity logging does not consume the body before the
                // downstream service (empty POST bodies previously caused write failures).
                return DataBufferUtils.join(super.getBody())
                        .flatMapMany(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            try {
                                String requestBody = new String(bytes, StandardCharsets.UTF_8);
                                activity.setRequestBody(requestBody);
                                String ip = request.getRemoteAddress() != null
                                        ? request.getRemoteAddress().getAddress().getHostAddress()
                                        : "unknown";
                                log.info(
                                        "User:{} from IP Address: {} for requestId: {}, request body :{}",
                                        activity.getUserId(),
                                        ip,
                                        request.getId(),
                                        JsonMaskerUtil.maskSensitiveFields(requestBody, "password"));
                            } catch (Exception e) {
                                log.info("Error logging request body: {}", e.getMessage());
                            }
                            return Flux.just(bufferFactory.wrap(bytes));
                        })
                        .switchIfEmpty(Flux.empty());
            }
        };
    }

    @Override
    public int getOrder() {
        // Run after Spring Security authentication (default is 0, so -1 ensures we run after)
        return -1;
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        exchange.getResponse().getHeaders().set(Constant.REQUEST_ID, req.getId());
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest().mutate().header(Constant.REQUEST_ID, req.getId()).build();
        String ipAddress = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();

        HttpHeaders headers = exchange.getRequest().getHeaders();

        // Check if it's a whitelisted endpoint first
        String path = request.getPath().toString();
        boolean isWhitelisted = isWhitelistedPath(path);

        // Get user ID from Spring Security context (no API call needed)
        Mono<String> userIdMono = ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> Mono.justOrEmpty(ctx.getAuthentication()))
                .filter(_ -> true)
                .cast(Authentication.class)
                .map(auth -> {
                    if (auth.getPrincipal() instanceof OAuth2AuthenticatedPrincipal principal) {
                        // Extract user ID from the principal (sub claim from introspection)
                        String sub = principal.getAttribute("sub");
                        return sub != null ? sub : auth.getName();
                    }
                    return auth.getName();
                })
                .defaultIfEmpty("")
                .mapNotNull(userId -> {
                    // If no user ID and not whitelisted, return null (will be handled below)
                    if (userId == null || userId.isEmpty()) {
                        return isWhitelisted ? "anonymous" : null;
                    }
                    return userId;
                });

        return userIdMono.flatMap(userId -> {
            if (userId == null && !isWhitelisted) {
                // Not authenticated and not whitelisted
                ApiErrorResponse apiResponse = ApiErrorResponse.builder()
                        .success(false)
                        .code("IAM006")
                        .message(Constant.UNAUTHORIZED_ACCESS)
                        .build();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.just(response.bufferFactory()
                        .wrap(new Gson().toJson(apiResponse).getBytes())));
            }

            // Use anonymous for whitelisted endpoints if no user ID
            String finalUserId = userId != null ? userId : "anonymous";

            // log the response body
            ActivityLogRequest activity = createActivityLog(request, ipAddress, finalUserId);

            return isBlocked(ipAddress)
                    .flatMap(blocked -> {
                        if (Boolean.TRUE.equals(blocked)) {
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            return requestLimitExceedMessage(response);
                        } else {
                            ApiErrorResponse apiResponse = checkXsrfToken(activity, headers, exchange);
                            if (!apiResponse.isSuccess()) {
                                return response.writeWith(Mono.just(response.bufferFactory()
                                        .wrap(new Gson().toJson(apiResponse).getBytes())));
                            }
                            return checkRateLimit(ipAddress)
                                    .flatMap(allowed -> validateRateLimit(activity, exchange, chain, allowed, ipAddress));
                        }
                    });
        });
    }

    private boolean isWhitelistedPath(String path) {
        String[] whitelistPatterns = {
                "/swagger-ui.html",
                "/swagger-ui/",
                "/swagger-resources/",
                "/v3/api-docs",
                "/webjars/",
                "/configuration/ui",
                "/configuration/security",
                "/login",
                "/actuator/",
                "/oauth2/",
                "/api/v2/user/login",
                "/api/v2/user/forgot/password",
                "/api/v2/user/reset-password/otp",
                "/api/v2/user/registration",
                "/api/v2/user/update/password",
                "/api/v2/user/resend/otp",
                "/api/v2/user/refresh/token",
                "/api/v2/public/",
                "/api/v2/user/email-verification",
                "/api/v2/public/company/resolve-tenant",
                "/",
                "/api/v2/bucket/retrieve/",
                "/api/v2/bucket/document/retrieve/",
                "/api/v2/auth/",
                "/api/v2/booking/",
                "/api/v2/catalog/",
                "/api/v2/provider/",
                "/api/v2/payment/",
                "/api/v2/postjob/",
                "/api/v2/review/",
                "/api/v2/helpdesk/",
                "/api/v2/notification/",
                "/api/v2/blog/",
                "/api/v2/config/",
                "/api/v2/geo/"
        };
        
        for (String pattern : whitelistPatterns) {
            if (path.equals(pattern) || path.startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }


    private ApiErrorResponse checkXsrfToken(ActivityLogRequest activity, HttpHeaders headers, ServerWebExchange exchange) {
        String url = activity.getUrl();
        boolean skipXsrf = url.contains("bucket/document/retrieve")
                || url.contains("swagger") || url.contains("webjars") || url.contains("api-docs") || url.contains("actuator")
                || url.contains("/api/v2/user/login")
                || url.contains("/api/v2/public/")
                || url.contains("/api/v2/public/company/resolve-tenant")
                || url.contains("/bucket/public/")
                || url.contains("/api/v2/auth/")
                || url.contains("/api/v2/catalog/")
                || url.contains("/api/v2/booking/")
                || url.contains("/api/v2/provider/")
                || url.contains("/api/v2/payment/")
                || url.contains("/api/v2/postjob/")
                || url.contains("/api/v2/review/")
                || url.contains("/api/v2/helpdesk/")
                || url.contains("/api/v2/geo/")
                || url.contains("/api/v2/config/")
                || url.contains("/api/v2/notification/")
                || url.contains("/api/v2/blog/")
                ;

        if (skipXsrf) {
            return ApiErrorResponse.builder().success(true).build();
        }
        if (xsrfToken == null || xsrfToken.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return ApiErrorResponse.builder().success(false).message(Constant.XSRF_TOKEN_MISSING).build();
        }
        String xToken = headers.getFirst(Constant.X_XSRF_TOKEN);
        if (xToken == null || xToken.isBlank()) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return ApiErrorResponse.builder().success(false).message(Constant.XSRF_TOKEN_MISSING).build();
        }
        try {
            String expected = xsrfToken.trim();
            String decrypted = aesEncryption.decrypt(xToken.trim());
            if (!Objects.equals(decrypted, expected)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return ApiErrorResponse.builder().success(false).message(Constant.INVALID_XSRF_TOKEN).build();
            }
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return ApiErrorResponse.builder().success(false).message(Constant.INVALID_XSRF_TOKEN_FORMAT).build();
        }
        return ApiErrorResponse.builder().success(true).build();
    }

    private Mono<Void> validateRateLimit(ActivityLogRequest activity, ServerWebExchange exchange, WebFilterChain chain, Boolean allowed, String ipAddress) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequestDecorator decoratedRequest = getDecoratedRequest(request, activity);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = getDecoratedResponse(response, activity, dataBufferFactory);
        if (Boolean.TRUE.equals(allowed)) {
            return chain.filter(exchange.mutate().request(decoratedRequest).response(request.getPath().toString().contains("retrieve-bucket") ? response : decoratedResponse).build())
                    .publishOn(Schedulers.boundedElastic())
                    .onErrorResume(throwable -> handleErrorResponse(throwable, exchange, activity));
        } else {
            blockIP(ipAddress);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return requestLimitExceedMessage(response);
        }
    }


    /**
     * Centralized error handling method to return structured JSON responses.
     */
    private Mono<Void> handleErrorResponse(Throwable throwable, ServerWebExchange exchange, ActivityLogRequest activity) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500
        String errorMessage = "An unexpected error occurred. If you continue experiencing issues, please reach out to our support team.";
        Throwable exceptionMessage = null;
        log.error("Throwable exception: {}", throwable.getMessage(), throwable);
        switch (throwable) {
            case NoResourceFoundException exception -> {
                status = HttpStatus.NOT_FOUND;
                exceptionMessage = exception;
            }
            case NotFoundException exception -> {
                log.error("Service not found for request [{}]: {}", exchange.getRequest().getURI().getPath(), exception.getMessage());
                String serviceName = extractServiceName(exception.getMessage());
                log.info("Service Name {} is temporally unavailable", serviceName);
                status = HttpStatus.SERVICE_UNAVAILABLE;
                errorMessage = "Service is temporarily unavailable. Please try again later.";
                exceptionMessage = exception;
            }
            case WebClientRequestException exception -> {
                log.error("Server unavailable for request [{}]: {}", exchange.getRequest().getURI().getPath(), throwable.getMessage());
                status = HttpStatus.SERVICE_UNAVAILABLE;
                String serviceName = extractServiceName(exception.getMessage());
                log.info("Service Name {} is temporally unavailable webclient", serviceName);
                errorMessage = "Service is temporarily unavailable. Please try again later.";
                exceptionMessage = exception;
            }
            case ConnectException ignored -> {
                status = HttpStatus.SERVICE_UNAVAILABLE;
                errorMessage = "Service is temporarily unavailable. Please try again later.";
                exceptionMessage = ignored;
            }
            case SocketException socketException -> {
                status = HttpStatus.SERVICE_UNAVAILABLE;
                errorMessage = "Service is temporarily unavailable. Please try again later.";
                exceptionMessage = socketException;
            }
            default -> log.error("Unhandled error processing request [{}]: {}", exchange.getRequest().getURI().getPath(), Objects.requireNonNull(throwable).getMessage(), throwable);
        }

        // Log activity for valid API URLs
        if (activity.getUrl().contains(Constant.VALID_API_URL)) {
            activity.setResponse(errorMessage);
            activity.setStatusCode(status.value());
            sendActivityLog(activity);
        }

        // Create a structured JSON response
        ApiErrorResponse apiResponse = ApiErrorResponse.builder().success(false).message(errorMessage).debugMessage(ExceptionUtil.getStackTraceString(Objects.requireNonNull(exceptionMessage))).build();
        String jsonResponse = new Gson().toJson(apiResponse);
        log.error("Returning custom error response for [{}]: {}", exchange.getRequest().getURI().getPath(), new Gson().toJson(apiResponse));

        response.setStatusCode(status);
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer)).then();
    }

    private String extractServiceName(String message) {
        try {
            int startIndex = message.indexOf("Unable to find instance for ");
            if (startIndex != -1) {
                String servicePart = message.substring(startIndex + "Unable to find instance for ".length());
                return servicePart.replace("\"", "").trim();
            }
        } catch (Exception e) {
            log.warn("Failed to extract service name from exception message: {}", message);
        }
        return "unknown-service";
    }


    private ActivityLogRequest createActivityLog(ServerHttpRequest request, String ipAddress, String userId) {
        String userAgent = request.getHeaders().getFirst(Constant.USER_AGENT);
        if (userAgent == null || userAgent.isBlank()) {
            userAgent = "unknown";
        }
        return ActivityLogRequest.builder()
                .userId(userId)
                .requestId(request.getId())
                .ipAddress(ipAddress)
                .requestDate(new Date())
                .url(request.getPath().toString())
                .requestHeader(request.getHeaders().toString())
                .method(request.getMethod() != null ? request.getMethod().name() : "UNKNOWN")
                .origin(userAgent)
                .device(request.getHeaders().getFirst(Constant.DEVICE_ID))
                .build();
    }

    public Mono<Void> requestLimitExceedMessage(ServerHttpResponse response) {
        ApiErrorResponse apiResponse = ApiErrorResponse.builder().success(false).message(Constant.REQUEST_LIMIT_EXCEED).build();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(new Gson().toJson(apiResponse).getBytes())));
    }

    private Mono<Boolean> isBlocked(String ip) {
        return redisTemplate.opsForValue()
                .get(ip)
                .map(Constant.BLOCKED::equals)
                .defaultIfEmpty(false);
    }

    private Mono<Boolean> checkRateLimit(String ip) {
        return redisTemplate.opsForValue().increment(ip)
                .flatMap(count -> {
                    if (count == 1) {
                        // Set expiration for the key
                        return redisTemplate.expire(ip, Duration.ofMinutes(rateLimitProperties.getRefillPeriod()))
                                .thenReturn(count);
                    } else {
                        return Mono.just(count);
                    }
                })
                .map(count -> count <= rateLimitProperties.getCapacity())
                .defaultIfEmpty(false);
    }

    private void blockIP(String ip) {
        redisTemplate.opsForValue()
                .set(ip, Constant.BLOCKED, Duration.ofMinutes(rateLimitProperties.getBlockDuration()))
                .subscribe();
    }

}
