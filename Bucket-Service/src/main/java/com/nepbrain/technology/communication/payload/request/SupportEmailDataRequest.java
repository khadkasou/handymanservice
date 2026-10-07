package com.nepbrain.technology.communication.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportEmailDataRequest extends PaginationRequest {

    private String email;
    private String name;
    private Boolean isCompany;
}
