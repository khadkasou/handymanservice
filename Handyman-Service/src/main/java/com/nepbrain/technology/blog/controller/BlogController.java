package com.nepbrain.technology.blog.controller;

import com.nepbrain.technology.blog.annotation.CustomBlogRestController;
import com.nepbrain.technology.blog.payload.request.BlogDetailRequest;
import com.nepbrain.technology.blog.service.BlogService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CustomBlogRestController
@RequiredArgsConstructor
@Tag(name = "Blog")
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/blog-list")
    @Operation(summary = "Blog list")
    public ResponseEntity<GlobalResponse> blogList() {
        return ResponseEntity.ok().body(blogService.blogList());
    }

    @PostMapping("/blog-detail")
    @Operation(summary = "Blog detail")
    public ResponseEntity<GlobalResponse> blogDetail(@RequestBody BlogDetailRequest request) {
        return ResponseEntity.ok().body(blogService.blogDetail(request.getId()));
    }
}
