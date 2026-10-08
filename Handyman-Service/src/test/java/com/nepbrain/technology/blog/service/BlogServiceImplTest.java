package com.nepbrain.technology.blog.service;

import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.nepbrain.technology.blog.entity.BlogEntity;
import com.nepbrain.technology.blog.payload.response.BlogResponse;
import com.nepbrain.technology.blog.repository.BlogRepository;
import com.nepbrain.technology.blog.service.impl.BlogServiceImpl;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BlogServiceImplTest {

    @Mock private BlogRepository blogRepository;

    @Spy private ModelMapper modelMapper = configuredMapper();

    @InjectMocks private BlogServiceImpl blogService;

    private static ModelMapper configuredMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        return mapper;
    }

    @Test
    void blogList_success() {
        BlogEntity blog = BlogEntity.builder()
                .title("Tips")
                .description("How to maintain pipes")
                .image("blog.png")
                .isFeatured(true)
                .status(StatusEnum.ACTIVE)
                .authorName("Admin")
                .build();
        blog.setId(UUID.randomUUID());
        when(blogRepository.findByStatus(StatusEnum.ACTIVE)).thenReturn(List.of(blog));

        List<BlogResponse> result = (List<BlogResponse>) blogService.blogList().getData();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Tips");
        assertThat(result.getFirst().getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getFirst().getAuthorName()).isEqualTo("Admin");
        verify(blogRepository).findByStatus(StatusEnum.ACTIVE);
    }

    @Test
    void blogDetail_success() {
        UUID id = UUID.randomUUID();
        BlogEntity blog = BlogEntity.builder()
                .title("Detail")
                .description("Full content")
                .status(StatusEnum.ACTIVE)
                .authorName("Editor")
                .build();
        blog.setId(id);
        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));

        BlogResponse response = (BlogResponse) blogService.blogDetail(id).getData();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getTitle()).isEqualTo("Detail");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        verify(blogRepository).findById(id);
    }

    @Test
    void blogDetail_notFound_throwsGlobalException() {
        UUID id = UUID.randomUUID();
        when(blogRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.blogDetail(id)).isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("BLG001");
    }
}
