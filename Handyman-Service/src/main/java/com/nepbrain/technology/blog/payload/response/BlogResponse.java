package com.nepbrain.technology.blog.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogResponse {

    private UUID id;
    private String title;
    private String description;
    private String image;
    private Boolean isFeatured;
    private String status;
    private String authorName;
}
