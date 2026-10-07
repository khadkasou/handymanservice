package com.nepbrain.technology.communication.payload.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingDetailResponse {

    private UUID id;
    private String key;
    private String value;
    private String displayName;
    private UUID settingCategoryId;
    private String settingCategoryCode;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
