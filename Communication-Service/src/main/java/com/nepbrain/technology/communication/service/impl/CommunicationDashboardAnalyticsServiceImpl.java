package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.MessageDetailEntity;
import com.nepbrain.technology.communication.entity.MessageEntity;
import com.nepbrain.technology.communication.entity.SupportEmailEntity;
import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.payload.response.CommunicationDashboardAnalyticsResponse;
import com.nepbrain.technology.communication.repository.MessageRepository;
import com.nepbrain.technology.communication.repository.SupportEmailRepository;
import com.nepbrain.technology.communication.service.CommunicationDashboardAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunicationDashboardAnalyticsServiceImpl implements CommunicationDashboardAnalyticsService {

    private static final DateTimeFormatter MONTH_LABEL =
            DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    private final SupportEmailRepository supportEmailRepository;
    private final MessageRepository messageRepository;

    @Override
    public CommunicationDashboardAnalyticsResponse getAnalytics() {
        LocalDateTime monthStart = YearMonth.now().atDay(1).atStartOfDay();

        return CommunicationDashboardAnalyticsResponse.builder()
                .supportMessages(CommunicationDashboardAnalyticsResponse.MetricSummary.builder()
                        .total(supportEmailRepository.count())
                        .thisMonth(supportEmailRepository.countByCreatedAtGreaterThanEqual(monthStart))
                        .open(supportEmailRepository.countOpenWithoutReplies())
                        .active(0L)
                        .build())
                .notices(CommunicationDashboardAnalyticsResponse.MetricSummary.builder()
                        .total(messageRepository.count())
                        .thisMonth(messageRepository.countByCreatedAtGreaterThanEqual(monthStart))
                        .open(0L)
                        .active(messageRepository.countByStatus(StatusEnum.ACTIVE))
                        .build())
                .supportMessageTrend(buildSupportTrend())
                .noticeTrend(buildNoticeTrend())
                .recentSupportMessages(mapRecentSupport())
                .recentNotices(mapRecentNotices())
                .build();
    }

    private List<CommunicationDashboardAnalyticsResponse.MonthlyCountPoint> buildSupportTrend() {
        return buildMonthlyTrend((from, to) -> supportEmailRepository.countCreatedBetween(from, to));
    }

    private List<CommunicationDashboardAnalyticsResponse.MonthlyCountPoint> buildNoticeTrend() {
        return buildMonthlyTrend((from, to) -> messageRepository.countCreatedBetween(from, to));
    }

    private List<CommunicationDashboardAnalyticsResponse.MonthlyCountPoint> buildMonthlyTrend(
            MonthlyCounter counter) {
        List<CommunicationDashboardAnalyticsResponse.MonthlyCountPoint> points = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int offset = 5; offset >= 0; offset--) {
            YearMonth month = current.minusMonths(offset);
            LocalDateTime from = month.atDay(1).atStartOfDay();
            LocalDateTime to = month.plusMonths(1).atDay(1).atStartOfDay();
            points.add(CommunicationDashboardAnalyticsResponse.MonthlyCountPoint.builder()
                    .month(month.toString())
                    .label(month.format(MONTH_LABEL))
                    .count(counter.count(from, to))
                    .build());
        }
        return points;
    }

    private List<CommunicationDashboardAnalyticsResponse.SupportMessageItem> mapRecentSupport() {
        return supportEmailRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 6)).stream()
                .map(this::toSupportItem)
                .toList();
    }

    private CommunicationDashboardAnalyticsResponse.SupportMessageItem toSupportItem(SupportEmailEntity entity) {
        return CommunicationDashboardAnalyticsResponse.SupportMessageItem.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .name(entity.getName())
                .email(entity.getEmail())
                .subject(entity.getSubject())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .build();
    }

    private List<CommunicationDashboardAnalyticsResponse.NoticeItem> mapRecentNotices() {
        return messageRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 6)).stream()
                .map(this::toNoticeItem)
                .toList();
    }

    private CommunicationDashboardAnalyticsResponse.NoticeItem toNoticeItem(MessageEntity entity) {
        String title = null;
        if (entity.getMessages() != null && !entity.getMessages().isEmpty()) {
            MessageDetailEntity detail = entity.getMessages().get(0);
            title = detail.getEmailSubject() != null ? detail.getEmailSubject() : detail.getNotificationSubject();
        }
        return CommunicationDashboardAnalyticsResponse.NoticeItem.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .topic(entity.getTopic() != null ? entity.getTopic().name() : null)
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .title(title)
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .build();
    }

    @FunctionalInterface
    private interface MonthlyCounter {
        long count(LocalDateTime from, LocalDateTime to);
    }
}
