package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunicationSearchFilterPaginationRequest extends PaginationRequest {
    private String searchText;
    private StatusEnum status;
    private ChannelEnum type;
    private LanguageEnum language;
    private String topic;
    private String fromDate;
    private String toDate;
}
