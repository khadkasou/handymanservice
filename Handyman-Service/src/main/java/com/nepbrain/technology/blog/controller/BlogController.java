package com.nepbrain.technology.blog.controller;
import com.nepbrain.technology.blog.annotation.CustomBlogRestController;
import com.nepbrain.technology.blog.payload.request.BlogDetailRequest;
import com.nepbrain.technology.blog.service.BlogService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;

@CustomBlogRestController @RequiredArgsConstructor
@Tag(name = "Blog", description = "Blog APIs")
public class BlogController {
    private final BlogService blogService;
    @GetMapping("/blog-list") public ResponseEntity<GlobalResponse> blogList() {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("BLOG_LIST", blogService.blogList()));
    }
    @PostMapping("/blog-detail") public ResponseEntity<GlobalResponse> blogDetail(@RequestBody BlogDetailRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("BLOG_DETAIL", blogService.blogDetail(request.getId())));
    }
}
