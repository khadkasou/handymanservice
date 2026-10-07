package com.nepbrain.technology.communication.payload.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingCategoryResponse {

    private UUID id;
    private String categoryCode;
    private String name;
    private String description;
    private UUID settingId;
    private String settingCode;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
