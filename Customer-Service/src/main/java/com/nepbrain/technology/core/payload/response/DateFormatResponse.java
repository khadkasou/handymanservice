package com.nepbrain.technology.core.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateFormatResponse {
    String createdAt;
    String lastModifiedAt;
}