package com.nepbrain.technology.postjob.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.postjob.payload.request.PostJobRequest;
import com.nepbrain.technology.postjob.payload.response.PostJobResponse;
import com.nepbrain.technology.postjob.service.PostJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostJobControllerTest {

    @Mock
    private PostJobService postJobService;

    @InjectMocks
    private PostJobController postJobController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postJobController).build();
    }

    @Test
    void savePostJob_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(postJobService.savePostJob(any(PostJobRequest.class))).thenReturn(PostJobResponse.builder()
                .id(id)
                .title("Fix sink")
                .price(new BigDecimal("500"))
                .status("OPEN")
                .build());

        mockMvc.perform(post("/api/v2/postjob/save-post-job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PostJobRequest.builder()
                                .title("Fix sink")
                                .customerId(UUID.randomUUID())
                                .price(new BigDecimal("500"))
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.code").value("SAVE_POST_JOB"))
                .andExpect(jsonPath("$.data.title").value("Fix sink"));

        verify(postJobService).savePostJob(any(PostJobRequest.class));
    }

    @Test
    void getPostJob_withCustomerId() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(postJobService.getPostJob(customerId)).thenReturn(List.of(PostJobResponse.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .title("Job")
                .build()));

        mockMvc.perform(get("/api/v2/postjob/get-post-job").param("customer_id", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("GET_POST_JOB"))
                .andExpect(jsonPath("$.data[0].customerId").value(customerId.toString()));

        verify(postJobService).getPostJob(eq(customerId));
    }

    @Test
    void getPostJob_withoutCustomerId() throws Exception {
        when(postJobService.getPostJob(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/postjob/get-post-job"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("GET_POST_JOB"));

        verify(postJobService).getPostJob(isNull());
    }

    @Test
    void getPostJobDetail_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(postJobService.getPostJobDetail(id)).thenReturn(PostJobResponse.builder()
                .id(id)
                .title("Detail")
                .build());

        mockMvc.perform(post("/api/v2/postjob/get-post-job-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("id", id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("GET_POST_JOB_DETAIL"))
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(postJobService).getPostJobDetail(eq(id));
    }

    @Test
    void postJobDelete_returnsSuccessMessage() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(postJobService).postJobDelete(id);

        mockMvc.perform(post("/api/v2/postjob/post-job-delete/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("POST_JOB_DELETE"))
                .andExpect(jsonPath("$.data").value("Post job deleted"));

        verify(postJobService).postJobDelete(eq(id));
    }
}
