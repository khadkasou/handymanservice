package com.nepbrain.technology.postjob.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.postjob.entity.PostJobEntity;
import com.nepbrain.technology.postjob.payload.request.PostJobRequest;
import com.nepbrain.technology.postjob.payload.response.PostJobResponse;
import com.nepbrain.technology.postjob.repository.PostJobRepository;
import com.nepbrain.technology.postjob.service.impl.PostJobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostJobServiceImplTest {

    @Mock private PostJobRepository postJobRepository;

    @Spy
    private ModelMapper modelMapper = configuredMapper();

    @InjectMocks
    private PostJobServiceImpl postJobService;

    private UUID customerId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
    }

    private static ModelMapper configuredMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        return mapper;
    }

    @Test
    void savePostJob_create_success() {
        PostJobRequest request = PostJobRequest.builder()
                .title("Fix sink")
                .description("Leaking kitchen sink")
                .customerId(customerId)
                .price(new BigDecimal("500"))
                .address("Kathmandu")
                .categoryId(categoryId)
                .status("active")
                .build();
        when(postJobRepository.save(any(PostJobEntity.class))).thenAnswer(invocation -> {
            PostJobEntity e = invocation.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        PostJobResponse response = postJobService.savePostJob(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Fix sink");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        verify(postJobRepository).save(any(PostJobEntity.class));
    }

    @Test
    void savePostJob_update_success() {
        UUID id = UUID.randomUUID();
        PostJobEntity existing = PostJobEntity.builder()
                .title("Old")
                .customerId(customerId)
                .status(StatusEnum.ACTIVE)
                .build();
        existing.setId(id);
        when(postJobRepository.findById(id)).thenReturn(Optional.of(existing));
        when(postJobRepository.save(any(PostJobEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        PostJobResponse response = postJobService.savePostJob(PostJobRequest.builder()
                .id(id)
                .title("Updated title")
                .build());

        assertThat(response.getTitle()).isEqualTo("Updated title");
        verify(postJobRepository).findById(id);
    }

    @Test
    void savePostJob_update_notFound_throwsGlobalException() {
        UUID id = UUID.randomUUID();
        when(postJobRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postJobService.savePostJob(PostJobRequest.builder().id(id).title("X").build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PJB001");
    }

    @Test
    void getPostJob_byCustomerId_success() {
        PostJobEntity entity = PostJobEntity.builder()
                .title("Job")
                .customerId(customerId)
                .status(StatusEnum.ACTIVE)
                .build();
        entity.setId(UUID.randomUUID());
        when(postJobRepository.findByCustomerId(customerId)).thenReturn(List.of(entity));

        List<PostJobResponse> result = postJobService.getPostJob(customerId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCustomerId()).isEqualTo(customerId);
        verify(postJobRepository).findByCustomerId(customerId);
        verify(postJobRepository, never()).findAll();
    }

    @Test
    void getPostJob_all_whenCustomerIdNull() {
        PostJobEntity entity = PostJobEntity.builder().title("Any").status(StatusEnum.ACTIVE).build();
        entity.setId(UUID.randomUUID());
        when(postJobRepository.findAll()).thenReturn(List.of(entity));

        List<PostJobResponse> result = postJobService.getPostJob(null);

        assertThat(result).hasSize(1);
        verify(postJobRepository).findAll();
    }

    @Test
    void getPostJobDetail_success() {
        UUID id = UUID.randomUUID();
        PostJobEntity entity = PostJobEntity.builder()
                .title("Detail")
                .customerId(customerId)
                .status(StatusEnum.ACTIVE)
                .build();
        entity.setId(id);
        when(postJobRepository.findById(id)).thenReturn(Optional.of(entity));

        PostJobResponse response = postJobService.getPostJobDetail(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getTitle()).isEqualTo("Detail");
        verify(postJobRepository).findById(id);
    }

    @Test
    void getPostJobDetail_notFound_throwsGlobalException() {
        UUID id = UUID.randomUUID();
        when(postJobRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postJobService.getPostJobDetail(id))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PJB001");
    }

    @Test
    void postJobDelete_success() {
        UUID id = UUID.randomUUID();
        when(postJobRepository.existsById(id)).thenReturn(true);

        postJobService.postJobDelete(id);

        verify(postJobRepository).deleteById(id);
    }

    @Test
    void postJobDelete_notFound_throwsGlobalException() {
        UUID id = UUID.randomUUID();
        when(postJobRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> postJobService.postJobDelete(id))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PJB001");
        verify(postJobRepository, never()).deleteById(any());
    }
}
