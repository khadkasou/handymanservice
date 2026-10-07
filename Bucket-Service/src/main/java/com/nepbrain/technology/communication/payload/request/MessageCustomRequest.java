package com.nepbrain.technology.communication.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
/*
 * @Created At 18/12/2024
 * @Author Suroj Awal
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageCustomRequest {

    private String title;
    private String body;
    @Builder.Default
    private String priority = "high";
    private String image;
    private Map<String, String> data;

}
