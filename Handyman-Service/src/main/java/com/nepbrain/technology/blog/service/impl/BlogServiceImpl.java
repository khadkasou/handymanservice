package com.nepbrain.technology.blog.service.impl;
import com.nepbrain.technology.blog.entity.BlogEntity;
import com.nepbrain.technology.blog.payload.response.BlogResponse;
import com.nepbrain.technology.blog.repository.BlogRepository;
import com.nepbrain.technology.blog.service.BlogService;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;
import static com.nepbrain.technology.blog.enums.ErrorCodeEnum.BLG001;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository; private final ModelMapper modelMapper;
    @Override public List<BlogResponse> blogList() {
        return blogRepository.findByStatus(StatusEnum.ACTIVE).stream().map(this::toRes).collect(Collectors.toList());
    }
    @Override public BlogResponse blogDetail(UUID id) {
        return toRes(blogRepository.findById(id).orElseThrow(() -> new GlobalException(BLG001.name())));
    }
    private BlogResponse toRes(BlogEntity e) {
        BlogResponse r = modelMapper.map(e, BlogResponse.class);
        r.setStatus(e.getStatus() != null ? e.getStatus().name() : null); return r;
    }
}
