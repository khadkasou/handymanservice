package com.nepbrain.technology.blog.service.impl;

import com.nepbrain.technology.blog.entity.BlogEntity;
import com.nepbrain.technology.blog.enums.ErrorCodeEnum;
import com.nepbrain.technology.blog.payload.response.BlogResponse;
import com.nepbrain.technology.blog.repository.BlogRepository;
import com.nepbrain.technology.blog.service.BlogService;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    public GlobalResponse blogList() {
        List<BlogResponse> blogs = new ArrayList<>();
        for (BlogEntity blog : blogRepository.findByStatus(StatusEnum.ACTIVE)) {
            blogs.add(toResponse(blog));
        }
        return GlobalResponseBuilder.buildSuccessResponse(blogs);
    }

    @Override
    public GlobalResponse blogDetail(UUID id) {
        BlogEntity blog = blogRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.BLG001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(toResponse(blog));
    }

    private BlogResponse toResponse(BlogEntity blog) {
        String status = blog.getStatus() == null ? null : blog.getStatus().name();
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .description(blog.getDescription())
                .image(blog.getImage())
                .isFeatured(blog.getIsFeatured())
                .status(status)
                .authorName(blog.getAuthorName())
                .build();
    }
}
