package com.nepbrain.technology.core.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {

    private Integer pageNo;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;
}
