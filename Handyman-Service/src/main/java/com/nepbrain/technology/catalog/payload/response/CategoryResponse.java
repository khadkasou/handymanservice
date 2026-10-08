package com.nepbrain.technology.catalog.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {

    private UUID id;
    private String name;
    private String description;
    private String image;
    private UUID parentId;
    private String status;
    private Boolean isFeatured;
}
