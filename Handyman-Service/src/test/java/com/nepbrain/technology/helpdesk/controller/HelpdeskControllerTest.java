package com.nepbrain.technology.helpdesk.controller;

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
import com.nepbrain.technology.helpdesk.payload.request.HelpdeskActivityRequest;
import com.nepbrain.technology.helpdesk.payload.request.HelpdeskTicketRequest;
import com.nepbrain.technology.helpdesk.payload.response.HelpdeskActivityResponse;
import com.nepbrain.technology.helpdesk.payload.response.HelpdeskTicketResponse;
import com.nepbrain.technology.helpdesk.service.HelpdeskService;
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
class HelpdeskControllerTest {

    @Mock private HelpdeskService helpdeskService;

    @InjectMocks private HelpdeskController helpdeskController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(helpdeskController).build();
    }

    @Test
    void helpdeskSave_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(helpdeskService.helpdeskSave(any(HelpdeskTicketRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(
                HelpdeskTicketResponse.builder()
                .id(id)
                .customerId(customerId)
                .subject("Issue")
                .status("OPEN")
                .build()).build());

        mockMvc.perform(post("/api/v2/helpdesk/helpdesk-save").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(HelpdeskTicketRequest.builder()
                .customerId(customerId)
                .subject("Issue")
                .description("Details")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.data.subject").value("Issue"));

        verify(helpdeskService).helpdeskSave(any(HelpdeskTicketRequest.class));
    }

    @Test
    void helpdeskList_withFilters() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(helpdeskService.helpdeskList("OPEN", customerId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(HelpdeskTicketResponse.builder()
                .id(UUID.randomUUID())
                .status("OPEN")
                .customerId(customerId)
                .build())).build());

        mockMvc.perform(get("/api/v2/helpdesk/helpdesk-list").param("status", "OPEN")
                .param("customer_id", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value("OPEN"));

        verify(helpdeskService).helpdeskList(eq("OPEN"), eq(customerId));
    }

    @Test
    void helpdeskList_withoutFilters() throws Exception {
        when(helpdeskService.helpdeskList(null, null)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of()).build());

        mockMvc.perform(get("/api/v2/helpdesk/helpdesk-list")).andExpect(status().isOk());

        verify(helpdeskService).helpdeskList(isNull(), isNull());
    }

    @Test
    void helpdeskDetail_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(helpdeskService.helpdeskDetail(id)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(HelpdeskTicketResponse.builder()
                .id(id)
                .subject("Detail")
                .status("OPEN")
                .build()).build());

        mockMvc.perform(get("/api/v2/helpdesk/helpdesk-detail").param("id", id.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(helpdeskService).helpdeskDetail(eq(id));
    }

    @Test
    void helpdeskActivitySave_returnsOk() throws Exception {
        UUID helpDeskId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();
        when(helpdeskService.helpdeskActivitySave(eq(helpDeskId), any(HelpdeskActivityRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(
                HelpdeskActivityResponse.builder()
                .id(activityId)
                .helpdeskId(helpDeskId)
                .messages("Reply")
                .build()).build());

        mockMvc.perform(post("/api/v2/helpdesk/helpdesk-activity-save/" + helpDeskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(HelpdeskActivityRequest.builder()
                .messages("Reply")
                .senderType("customer")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.messages").value("Reply"));

        verify(helpdeskService).helpdeskActivitySave(eq(helpDeskId), any(HelpdeskActivityRequest.class));
    }

    @Test
    void helpdeskClosed_returnsSuccessMessage() throws Exception {
        UUID helpDeskId = UUID.randomUUID();
        when(helpdeskService.helpdeskClosed(helpDeskId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).message("Ticket closed").build());

        mockMvc.perform(post("/api/v2/helpdesk/helpdesk-closed/" + helpDeskId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ticket closed"));

        verify(helpdeskService).helpdeskClosed(eq(helpDeskId));
    }
}
