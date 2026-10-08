package com.nepbrain.technology.helpdesk.service;

import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.helpdesk.entity.HelpdeskActivityEntity;
import com.nepbrain.technology.helpdesk.entity.HelpdeskTicketEntity;
import com.nepbrain.technology.helpdesk.payload.request.HelpdeskActivityRequest;
import com.nepbrain.technology.helpdesk.payload.request.HelpdeskTicketRequest;
import com.nepbrain.technology.helpdesk.payload.response.HelpdeskActivityResponse;
import com.nepbrain.technology.helpdesk.payload.response.HelpdeskTicketResponse;
import com.nepbrain.technology.helpdesk.repository.HelpdeskActivityRepository;
import com.nepbrain.technology.helpdesk.repository.HelpdeskTicketRepository;
import com.nepbrain.technology.helpdesk.service.impl.HelpdeskServiceImpl;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class HelpdeskServiceImplTest {

    @Mock private HelpdeskTicketRepository ticketRepository;
    @Mock private HelpdeskActivityRepository activityRepository;

    @Spy private ModelMapper modelMapper = configuredMapper();

    @InjectMocks private HelpdeskServiceImpl helpdeskService;

    private UUID customerId;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
    }

    private static ModelMapper configuredMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        return mapper;
    }

    @Test
    void helpdeskSave_success_defaultsOpenStatus() {
        HelpdeskTicketRequest request = HelpdeskTicketRequest.builder()
                .customerId(customerId)
                .subject("Need help")
                .description("Booking issue")
                .mode("email")
                .type("support")
                .build();
        when(ticketRepository.save(any(HelpdeskTicketEntity.class))).thenAnswer(invocation -> {
                HelpdeskTicketEntity e = invocation.getArgument(0);
                if (e.getId() == null) {
                e.setId(UUID.randomUUID());
        }
                return e;
        });

        HelpdeskTicketResponse response = (HelpdeskTicketResponse) helpdeskService.helpdeskSave(request).getData();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getSubject()).isEqualTo("Need help");
        assertThat(response.getStatus()).isEqualTo("open");
        verify(ticketRepository).save(argThat(t -> "open".equals(t.getStatus())));
    }

    @Test
    void helpdeskList_byStatus() {
        HelpdeskTicketEntity ticket = HelpdeskTicketEntity.builder()
                .customerId(customerId)
                .subject("S")
                .status("open")
                .build();
        ticket.setId(ticketId);
        when(ticketRepository.findByStatus("open")).thenReturn(List.of(ticket));

        List<HelpdeskTicketResponse> result = (List<HelpdeskTicketResponse>) helpdeskService.helpdeskList("open", null).getData();
        assertThat(result).hasSize(1);
        verify(ticketRepository).findByStatus("open");
        verify(ticketRepository, never()).findByCustomerId(any());
    }

    @Test
    void helpdeskList_byCustomerId() {
        HelpdeskTicketEntity ticket = HelpdeskTicketEntity.builder()
                .customerId(customerId)
                .subject("S")
                .status("open")
                .build();
        ticket.setId(ticketId);
        when(ticketRepository.findByCustomerId(customerId)).thenReturn(List.of(ticket));

        List<HelpdeskTicketResponse> result = (List<HelpdeskTicketResponse>) helpdeskService.helpdeskList(null, customerId).getData();
        assertThat(result).hasSize(1);
        verify(ticketRepository).findByCustomerId(customerId);
    }

    @Test
    void helpdeskList_all() {
        when(ticketRepository.findAll()).thenReturn(List.of());

        List<HelpdeskTicketResponse> result = (List<HelpdeskTicketResponse>) helpdeskService.helpdeskList("  ", null).getData();
        assertThat(result).isEmpty();
        verify(ticketRepository).findAll();
    }

    @Test
    void helpdeskDetail_success_includesActivities() {
        HelpdeskTicketEntity ticket = HelpdeskTicketEntity.builder()
                .customerId(customerId)
                .subject("Subject")
                .status("open")
                .build();
        ticket.setId(ticketId);
        HelpdeskActivityEntity activity = HelpdeskActivityEntity.builder()
                .helpdeskId(ticketId)
                .senderId(customerId)
                .messages("Hello")
                .senderType("customer")
                .activityType("message")
                .build();
        activity.setId(UUID.randomUUID());

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(activityRepository.findByHelpdeskId(ticketId)).thenReturn(List.of(activity));

        HelpdeskTicketResponse response = (HelpdeskTicketResponse) helpdeskService.helpdeskDetail(ticketId).getData();
        assertThat(response.getId()).isEqualTo(ticketId);
        assertThat(response.getActivities()).hasSize(1);
        assertThat(response.getActivities().getFirst().getMessages()).isEqualTo("Hello");
        verify(activityRepository).findByHelpdeskId(ticketId);
    }

    @Test
    void helpdeskDetail_notFound_throwsGlobalException() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> helpdeskService.helpdeskDetail(ticketId)).isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("HLP001");
    }

    @Test
    void helpdeskActivitySave_success() {
        HelpdeskTicketEntity ticket = HelpdeskTicketEntity.builder()
                .customerId(customerId)
                .subject("S")
                .status("open")
                .build();
        ticket.setId(ticketId);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(activityRepository.save(any(HelpdeskActivityEntity.class))).thenAnswer(invocation -> {
                HelpdeskActivityEntity e = invocation.getArgument(0);
                if (e.getId() == null) {
                e.setId(UUID.randomUUID());
        }
                return e;
        });

        HelpdeskActivityRequest request = HelpdeskActivityRequest.builder()
                .senderId(customerId)
                .messages("Follow up")
                .senderType("admin")
                .build();

        HelpdeskActivityResponse response = (HelpdeskActivityResponse) helpdeskService.helpdeskActivitySave(ticketId, request).getData();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getMessages()).isEqualTo("Follow up");
        assertThat(response.getActivityType()).isEqualTo("message");
        verify(activityRepository).save(any(HelpdeskActivityEntity.class));
    }

    @Test
    void helpdeskActivitySave_ticketNotFound_throwsGlobalException() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> helpdeskService.helpdeskActivitySave(ticketId,
                HelpdeskActivityRequest.builder().messages("x").build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("HLP001");
    }

    @Test
    void helpdeskClosed_success() {
        HelpdeskTicketEntity ticket = HelpdeskTicketEntity.builder()
                .customerId(customerId)
                .subject("S")
                .status("open")
                .build();
        ticket.setId(ticketId);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(HelpdeskTicketEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        helpdeskService.helpdeskClosed(ticketId);

        assertThat(ticket.getStatus()).isEqualTo("closed");
        verify(ticketRepository).save(ticket);
    }

    @Test
    void helpdeskClosed_notFound_throwsGlobalException() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> helpdeskService.helpdeskClosed(ticketId)).isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("HLP001");
    }
}
