package com.nepbrain.technology.catalog.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavouriteRequest {

    private UUID userId;
    private UUID serviceId;
}
