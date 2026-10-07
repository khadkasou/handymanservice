package com.nepbrain.technology.communication.util;

/*
 * @Created At 05/06/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.communication.MasterServiceCommunication;
import com.nepbrain.technology.communication.payload.request.PaginationRequest;
import com.nepbrain.technology.communication.payload.response.DateFormatResponse;
import com.nepbrain.technology.communication.payload.response.SystemConfigResponse;
import com.nepbrain.technology.communication.payload.response.UserList;
import com.nepbrain.technology.communication.payload.response.UsernameResponse;
import com.nepbrain.technology.communication.webclient.UserCommunication;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nepbrain.technology.communication.util.Constant.DEFAULT_DATE_SS;

@Service
@RequiredArgsConstructor
public class Helper {

    private final MasterServiceCommunication master;
    private final UserCommunication userCommunication;

    public String extractSystemDateFormat() {
        SystemConfigResponse systemConfigResponse = master.getSystemConfig();
        if (systemConfigResponse != null) {
            return systemConfigResponse.getDateFormat();
        }
        return DEFAULT_DATE_SS;
    }

    public String formatDateTime(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    public String formatInstant(Instant instant, String format) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(zonedDateTime);
    }

    public List<UserList> getCreatedBy(List<UUID> createdById) {
        if (createdById != null) {
            GlobalResponse GlobalResponse = userCommunication.getUserDetail(createdById);
            Type listType = new TypeToken<List<UserList>>() {

            }.getType();
            return new ModelMapper().map(GlobalResponse.getData(), listType);
        }
        return new ArrayList<>();
    }

    public List<UserList> getUpdatedBy(List<UUID> updatedById) {
        if (updatedById != null) {
            GlobalResponse GlobalResponse = userCommunication.getUserDetail(updatedById);
            Type listType = new TypeToken<List<UserList>>() {

            }.getType();
            return new ModelMapper().map(GlobalResponse.getData(), listType);
        }
        return new ArrayList<>();
    }

    public DateFormatResponse getFormattedDate(LocalDateTime createdDate, LocalDateTime updateDate,
            String systemFormat) {
        DateTimeFormatter formatter;
        DateFormatResponse dateFormatResponse = new DateFormatResponse();
        if (systemFormat == null) {
            formatter = DateTimeFormatter.ofPattern(Constant.DEFAULT_DATE);
            dateFormatResponse.setCreatedAt(createdDate.format(formatter));
            dateFormatResponse.setLastModifiedAt(updateDate.format(formatter));
            return dateFormatResponse;
        }
        formatter = DateTimeFormatter.ofPattern(systemFormat);
        dateFormatResponse.setCreatedAt(createdDate.format(formatter));
        dateFormatResponse.setLastModifiedAt(updateDate != null ? updateDate.format(formatter) : null);
        return dateFormatResponse;
    }

    public Map<UUID, String> getUsernames(List<UUID> userId) {
        GlobalResponse userList = userCommunication.getUserDetail(userId);
        Type listType = new TypeToken<List<UsernameResponse>>() {

        }.getType();
        List<UsernameResponse> username = new ModelMapper().map(userList.getData(), listType);
        Map<UUID, String> usernameResponseMap = new HashMap<>();
        username.forEach(user -> usernameResponseMap.put(user.getId(), Stream.of(user.getName())
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.joining(" "))));
        return usernameResponseMap;

    }

    public static Pageable getPagination(PaginationRequest filterRequestCustomDto) {
        return PageRequest.of(filterRequestCustomDto.getPageNo(), filterRequestCustomDto.getPageSize(),
                Sort.by(Objects.equals(filterRequestCustomDto.getDirection(), "asc") ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        filterRequestCustomDto.getSortBy() == null ? "createdAt" : filterRequestCustomDto.getSortBy()));
    }




}
