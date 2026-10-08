package com.nepbrain.technology.helpdesk.service;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.helpdesk.payload.request.*;

import java.util.UUID;

public interface HelpdeskService {

    GlobalResponse helpdeskSave(HelpdeskTicketRequest request);

    GlobalResponse helpdeskList(String status, UUID customerId);

    GlobalResponse helpdeskDetail(UUID id);

    GlobalResponse helpdeskActivitySave(UUID helpdeskId, HelpdeskActivityRequest request);

    GlobalResponse helpdeskClosed(UUID helpdeskId);
}
