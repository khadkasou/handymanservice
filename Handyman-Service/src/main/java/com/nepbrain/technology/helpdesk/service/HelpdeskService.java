package com.nepbrain.technology.helpdesk.service;
import com.nepbrain.technology.helpdesk.payload.request.*;
import com.nepbrain.technology.helpdesk.payload.response.*;
import java.util.List; import java.util.UUID;
public interface HelpdeskService {
    HelpdeskTicketResponse helpdeskSave(HelpdeskTicketRequest request);
    List<HelpdeskTicketResponse> helpdeskList(String status, UUID customerId);
    HelpdeskTicketResponse helpdeskDetail(UUID id);
    HelpdeskActivityResponse helpdeskActivitySave(UUID helpdeskId, HelpdeskActivityRequest request);
    void helpdeskClosed(UUID helpdeskId);
}
