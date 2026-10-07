package com.nepbrain.technology.communication.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
