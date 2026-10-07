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
public class SupportEmailReplyResponse {

    private UUID id;
    private String message;
    private LocalDateTime createdAt;
}
