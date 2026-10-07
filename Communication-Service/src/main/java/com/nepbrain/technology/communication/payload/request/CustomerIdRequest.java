package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 14/06/2024
 * @Author suroj.awal
 */

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerIdRequest {

    private List<String> data;
}
