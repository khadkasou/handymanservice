package com.nepbrain.technology.core.payload;

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
