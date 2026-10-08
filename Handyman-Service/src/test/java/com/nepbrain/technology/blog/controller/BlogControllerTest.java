package com.nepbrain.technology.blog.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.blog.payload.request.BlogDetailRequest;
import com.nepbrain.technology.blog.payload.response.BlogResponse;
import com.nepbrain.technology.blog.service.BlogService;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BlogControllerTest {

    @Mock private BlogService blogService;

    @InjectMocks private BlogController blogController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(blogController).build();
    }

    @Test
    void blogList_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(blogService.blogList()).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(BlogResponse.builder()
                .id(id)
                .title("Tips")
                .status("ACTIVE")
                .build())).build());

        mockMvc.perform(get("/api/v2/blog/blog-list")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data[0].title").value("Tips"));

        verify(blogService).blogList();
    }

    @Test
    void blogDetail_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(blogService.blogDetail(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(
                BlogResponse.builder().id(id).title("Detail").description("Body").build()).build());

        mockMvc.perform(post("/api/v2/blog/blog-detail").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(BlogDetailRequest.builder().id(id).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.title").value("Detail"));

        verify(blogService).blogDetail(eq(id));
    }
}
