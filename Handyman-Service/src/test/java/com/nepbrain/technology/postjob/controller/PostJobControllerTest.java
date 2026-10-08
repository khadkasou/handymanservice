package com.nepbrain.technology.postjob.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
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

@ExtendWith(MockitoExtension.class)
class PostJobControllerTest {

    @Mock private PostJobService postJobService;

    @InjectMocks private PostJobController postJobController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postJobController).build();
    }

    @Test
    void savePostJob_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(postJobService.savePostJob(any(PostJobRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(PostJobResponse.builder()
                .id(id)
                .title("Fix sink")
                .price(new BigDecimal("500"))
                .status("OPEN")
                .build()).build());

        mockMvc.perform(post("/api/v2/postjob/save-post-job").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(PostJobRequest.builder()
                .title("Fix sink")
                .customerId(UUID.randomUUID())
                .price(new BigDecimal("500"))
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data.title").value("Fix sink"));

        verify(postJobService).savePostJob(any(PostJobRequest.class));
    }

    @Test
    void getPostJob_withCustomerId() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(postJobService.getPostJob(customerId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(PostJobResponse.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .title("Job")
                .build())).build());

        mockMvc.perform(get("/api/v2/postjob/get-post-job").param("customer_id", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customerId").value(customerId.toString()));

        verify(postJobService).getPostJob(eq(customerId));
    }

    @Test
    void getPostJob_withoutCustomerId() throws Exception {
        when(postJobService.getPostJob(null)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of()).build());

        mockMvc.perform(get("/api/v2/postjob/get-post-job")).andExpect(status().isOk());

        verify(postJobService).getPostJob(isNull());
    }

    @Test
    void getPostJobDetail_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(postJobService.getPostJobDetail(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(PostJobResponse.builder().id(id).title("Detail").build()).build());

        mockMvc.perform(post("/api/v2/postjob/get-post-job-detail").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("id", id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(postJobService).getPostJobDetail(eq(id));
    }

    @Test
    void postJobDelete_returnsSuccessMessage() throws Exception {
        UUID id = UUID.randomUUID();
        when(postJobService.postJobDelete(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Post job deleted").build());

        mockMvc.perform(post("/api/v2/postjob/post-job-delete/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post job deleted"));

        verify(postJobService).postJobDelete(eq(id));
    }
}
