package com.nepbrain.technology.blog.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogDetailRequest {

    private UUID id;
}
