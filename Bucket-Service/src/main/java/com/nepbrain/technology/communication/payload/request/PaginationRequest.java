package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 11/06/2024
 * @Author suroj.awal
 */

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {

    private int pageNo;
    private int pageSize;
    private String sortBy;
    private String direction;
}
