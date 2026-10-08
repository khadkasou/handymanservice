package com.nepbrain.technology.config.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirebaseDetailResponse {

    private String apiKey;
    private String appId;
    private String messagingSenderId;
    private String projectId;
    private String storageBucket;
}
