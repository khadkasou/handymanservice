package com.nepbrain.technology.helpdesk.service.impl;

import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.helpdesk.entity.HelpdeskActivityEntity;
import com.nepbrain.technology.helpdesk.entity.HelpdeskTicketEntity;
import com.nepbrain.technology.helpdesk.enums.ErrorCodeEnum;
import com.nepbrain.technology.helpdesk.payload.request.HelpdeskActivityRequest;
import com.nepbrain.technology.helpdesk.payload.request.HelpdeskTicketRequest;
import com.nepbrain.technology.helpdesk.payload.response.HelpdeskActivityResponse;
import com.nepbrain.technology.helpdesk.payload.response.HelpdeskTicketResponse;
import com.nepbrain.technology.helpdesk.repository.HelpdeskActivityRepository;
import com.nepbrain.technology.helpdesk.repository.HelpdeskTicketRepository;
import com.nepbrain.technology.helpdesk.service.HelpdeskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HelpdeskServiceImpl implements HelpdeskService {

    private final HelpdeskTicketRepository ticketRepository;
    private final HelpdeskActivityRepository activityRepository;

    @Override
    public GlobalResponse helpdeskSave(HelpdeskTicketRequest request) {
        HelpdeskTicketEntity ticket = new HelpdeskTicketEntity();
        ticket.setCustomerId(request.getCustomerId());
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setMode(request.getMode());
        ticket.setType(request.getType());
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            ticket.setStatus("open");
        } else {
            ticket.setStatus(request.getStatus());
        }
        if (request.getId() != null) {
            ticket.setId(request.getId());
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.helpdesk.save.success", toTicketResponse(ticketRepository.save(ticket)));
    }

    @Override
    public GlobalResponse helpdeskList(String status, UUID customerId) {
        List<HelpdeskTicketEntity> tickets;
        if (status != null && !status.isBlank()) {
            tickets = ticketRepository.findByStatus(status);
        } else if (customerId != null) {
            tickets = ticketRepository.findByCustomerId(customerId);
        } else {
            tickets = ticketRepository.findAll();
        }

        List<HelpdeskTicketResponse> result = new ArrayList<>();
        for (HelpdeskTicketEntity ticket : tickets) {
            result.add(toTicketResponse(ticket));
        }
        return GlobalResponseBuilder.buildSuccessResponse(result);
    }

    @Override
    public GlobalResponse helpdeskDetail(UUID id) {
        HelpdeskTicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.HLP001.name()));

        HelpdeskTicketResponse response = toTicketResponse(ticket);
        List<HelpdeskActivityResponse> activities = new ArrayList<>();
        for (HelpdeskActivityEntity activity : activityRepository.findByHelpdeskId(id)) {
            activities.add(toActivityResponse(activity));
        }
        response.setActivities(activities);
        return GlobalResponseBuilder.buildSuccessResponse(response);
    }

    @Override
    public GlobalResponse helpdeskActivitySave(UUID helpdeskId, HelpdeskActivityRequest request) {
        ticketRepository.findById(helpdeskId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.HLP001.name()));

        HelpdeskActivityEntity activity = HelpdeskActivityEntity.builder()
                .helpdeskId(helpdeskId)
                .senderId(request.getSenderId())
                .messages(request.getMessages())
                .senderType(request.getSenderType())
                .activityType("message")
                .build();
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.helpdesk.activity.saved.success", toActivityResponse(activityRepository.save(activity)));
    }

    @Override
    public GlobalResponse helpdeskClosed(UUID helpdeskId) {
        HelpdeskTicketEntity ticket = ticketRepository.findById(helpdeskId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.HLP001.name()));
        ticket.setStatus("closed");
        ticketRepository.save(ticket);
        return GlobalResponseBuilder.buildSuccessResponse("message.helpdesk.closed.success");
    }

    private HelpdeskTicketResponse toTicketResponse(HelpdeskTicketEntity ticket) {
        return HelpdeskTicketResponse.builder()
                .id(ticket.getId())
                .customerId(ticket.getCustomerId())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .mode(ticket.getMode())
                .type(ticket.getType())
                .build();
    }

    private HelpdeskActivityResponse toActivityResponse(HelpdeskActivityEntity activity) {
        return HelpdeskActivityResponse.builder()
                .id(activity.getId())
                .helpdeskId(activity.getHelpdeskId())
                .senderId(activity.getSenderId())
                .messages(activity.getMessages())
                .senderType(activity.getSenderType())
                .activityType(activity.getActivityType())
                .build();
    }
}
