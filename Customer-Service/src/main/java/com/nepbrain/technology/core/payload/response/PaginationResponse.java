package com.nepbrain.technology.core.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse {
    public long totalElements;
    private Object result;
}
