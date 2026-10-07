package com.nepbrain.technology.communication.payload.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingResponse {

    private UUID id;
    private String settingCode;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
