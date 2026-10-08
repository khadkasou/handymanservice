package com.nepbrain.technology.blog.service;

import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface BlogService {

    GlobalResponse blogList();

    GlobalResponse blogDetail(UUID id);
}
