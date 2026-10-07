package com.nepbrain.technology.blog.service;
import com.nepbrain.technology.blog.payload.response.BlogResponse;
import java.util.List; import java.util.UUID;
public interface BlogService {
    List<BlogResponse> blogList();
    BlogResponse blogDetail(UUID id);
}
