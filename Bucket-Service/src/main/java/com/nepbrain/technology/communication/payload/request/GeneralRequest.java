package com.nepbrain.technology.communication.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralRequest {

    private UUID itemId;
    private String categoryKey;
}
