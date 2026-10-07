package com.nepbrain.technology.bucket.payload.response;

import lombok.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

/*
 * @Created At 05/02/2025
 * @Author suroj.awal
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BucketResponse {
    private String message;
    private HttpHeaders headers;
    private InputStreamResource resource;
}
