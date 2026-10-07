package com.nepbrain.technology.postjob.service.impl;
import com.nepbrain.technology.core.enums.StatusEnum; import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.postjob.entity.PostJobEntity;
import com.nepbrain.technology.postjob.payload.request.PostJobRequest;
import com.nepbrain.technology.postjob.payload.response.PostJobResponse;
import com.nepbrain.technology.postjob.repository.PostJobRepository;
import com.nepbrain.technology.postjob.service.PostJobService;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;
import static com.nepbrain.technology.postjob.enums.ErrorCodeEnum.PJB001;

@Service @RequiredArgsConstructor @Transactional
public class PostJobServiceImpl implements PostJobService {
    private final PostJobRepository postJobRepository; private final ModelMapper modelMapper;
    @Override public PostJobResponse savePostJob(PostJobRequest request) {
        PostJobEntity e = request.getId() != null
                ? postJobRepository.findById(request.getId()).orElseThrow(() -> new GlobalException(PJB001.name()))
                : PostJobEntity.builder().status(StatusEnum.ACTIVE).build();
        if (request.getTitle() != null) e.setTitle(request.getTitle());
        if (request.getDescription() != null) e.setDescription(request.getDescription());
        if (request.getCustomerId() != null) e.setCustomerId(request.getCustomerId());
        if (request.getPrice() != null) e.setPrice(request.getPrice());
        if (request.getAddress() != null) e.setAddress(request.getAddress());
        if (request.getCategoryId() != null) e.setCategoryId(request.getCategoryId());
        if (request.getStatus() != null) e.setStatus(StatusEnum.valueOf(request.getStatus().toUpperCase()));
        return toRes(postJobRepository.save(e));
    }
    @Override @Transactional(readOnly = true)
    public List<PostJobResponse> getPostJob(UUID customerId) {
        List<PostJobEntity> list = customerId != null ? postJobRepository.findByCustomerId(customerId) : postJobRepository.findAll();
        return list.stream().map(this::toRes).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public PostJobResponse getPostJobDetail(UUID id) {
        return toRes(postJobRepository.findById(id).orElseThrow(() -> new GlobalException(PJB001.name())));
    }
    @Override public void postJobDelete(UUID id) {
        if (!postJobRepository.existsById(id)) throw new GlobalException(PJB001.name());
        postJobRepository.deleteById(id);
    }
    private PostJobResponse toRes(PostJobEntity e) {
        PostJobResponse r = modelMapper.map(e, PostJobResponse.class);
        r.setStatus(e.getStatus() != null ? e.getStatus().name() : null); return r;
    }
}
