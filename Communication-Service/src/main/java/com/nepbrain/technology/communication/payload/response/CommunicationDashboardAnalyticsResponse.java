package com.nepbrain.technology.communication.payload.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunicationDashboardAnalyticsResponse {

    private MetricSummary supportMessages;
    private MetricSummary notices;
    private List<MonthlyCountPoint> supportMessageTrend;
    private List<MonthlyCountPoint> noticeTrend;
    private List<SupportMessageItem> recentSupportMessages;
    private List<NoticeItem> recentNotices;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MetricSummary {
        private long total;
        private long thisMonth;
        private long open;
        private long active;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MonthlyCountPoint {
        private String month;
        private String label;
        private long count;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SupportMessageItem {
        private String id;
        private String name;
        private String email;
        private String subject;
        private String createdAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class NoticeItem {
        private String id;
        private String topic;
        private String status;
        private String title;
        private String createdAt;
    }
}
