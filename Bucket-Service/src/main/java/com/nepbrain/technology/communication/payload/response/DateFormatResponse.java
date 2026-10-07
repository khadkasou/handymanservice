package com.nepbrain.technology.communication.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DateFormatResponse {

    String createdAt;
    String lastModifiedAt;
}