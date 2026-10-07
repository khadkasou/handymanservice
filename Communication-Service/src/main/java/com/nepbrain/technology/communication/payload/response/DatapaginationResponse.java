package com.nepbrain.technology.communication.payload.response;

/*
 * @Created At 11/06/2024
 * @Author suroj.awal
 */

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatapaginationResponse {

    private long totalElementCount;
    private List<?> result;

}
