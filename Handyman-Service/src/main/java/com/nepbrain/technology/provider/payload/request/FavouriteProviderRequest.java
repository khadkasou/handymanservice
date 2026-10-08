package com.nepbrain.technology.provider.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavouriteProviderRequest {

    private UUID userId;
    private UUID providerId;
}
