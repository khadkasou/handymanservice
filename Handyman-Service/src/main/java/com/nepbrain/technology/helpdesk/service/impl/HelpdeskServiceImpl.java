package com.nepbrain.technology.helpdesk.service.impl;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.helpdesk.entity.*;
import com.nepbrain.technology.helpdesk.payload.request.*;
import com.nepbrain.technology.helpdesk.payload.response.*;
import com.nepbrain.technology.helpdesk.repository.*;
import com.nepbrain.technology.helpdesk.service.HelpdeskService;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;
import static com.nepbrain.technology.helpdesk.enums.ErrorCodeEnum.HLP001;

@Service @RequiredArgsConstructor @Transactional
public class HelpdeskServiceImpl implements HelpdeskService {
    private final HelpdeskTicketRepository ticketRepository;
    private final HelpdeskActivityRepository activityRepository;
    private final ModelMapper modelMapper;

    @Override public HelpdeskTicketResponse helpdeskSave(HelpdeskTicketRequest request) {
        HelpdeskTicketEntity e = modelMapper.map(request, HelpdeskTicketEntity.class);
        if (e.getStatus() == null) e.setStatus("open");
        return modelMapper.map(ticketRepository.save(e), HelpdeskTicketResponse.class);
    }
    @Override @Transactional(readOnly = true)
    public List<HelpdeskTicketResponse> helpdeskList(String status, UUID customerId) {
        List<HelpdeskTicketEntity> list;
        if (status != null && !status.isBlank()) list = ticketRepository.findByStatus(status);
        else if (customerId != null) list = ticketRepository.findByCustomerId(customerId);
        else list = ticketRepository.findAll();
        return list.stream().map(t -> modelMapper.map(t, HelpdeskTicketResponse.class)).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public HelpdeskTicketResponse helpdeskDetail(UUID id) {
        HelpdeskTicketEntity t = ticketRepository.findById(id).orElseThrow(() -> new GlobalException(HLP001.name()));
        HelpdeskTicketResponse r = modelMapper.map(t, HelpdeskTicketResponse.class);
        r.setActivities(activityRepository.findByHelpdeskId(id).stream()
                .map(a -> modelMapper.map(a, HelpdeskActivityResponse.class)).collect(Collectors.toList()));
        return r;
    }
    @Override public HelpdeskActivityResponse helpdeskActivitySave(UUID helpdeskId, HelpdeskActivityRequest request) {
        ticketRepository.findById(helpdeskId).orElseThrow(() -> new GlobalException(HLP001.name()));
        HelpdeskActivityEntity e = HelpdeskActivityEntity.builder()
                .helpdeskId(helpdeskId).senderId(request.getSenderId())
                .messages(request.getMessages()).senderType(request.getSenderType())
                .activityType("message").build();
        return modelMapper.map(activityRepository.save(e), HelpdeskActivityResponse.class);
    }
    @Override public void helpdeskClosed(UUID helpdeskId) {
        HelpdeskTicketEntity t = ticketRepository.findById(helpdeskId).orElseThrow(() -> new GlobalException(HLP001.name()));
        t.setStatus("closed"); ticketRepository.save(t);
    }
}
