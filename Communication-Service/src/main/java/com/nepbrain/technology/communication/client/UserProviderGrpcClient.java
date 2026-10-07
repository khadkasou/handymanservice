package com.nepbrain.technology.communication.client;

import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.user.grpc.GetUsersForBulkMessageRequest;
import com.nepbrain.technology.user.grpc.GetUsersForBulkMessageResponse;
import com.nepbrain.technology.user.grpc.UserContactInfo;
import com.nepbrain.technology.user.grpc.UserProviderServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserProviderGrpcClient {

    @GrpcClient("user-service")
    private UserProviderServiceGrpc.UserProviderServiceBlockingStub userProviderStub;

    /**
     * Fetch users for bulk messaging: all, by role IDs, or by user IDs.
     */
    public List<UserContactDto> getUsersForBulkMessage(boolean fetchAll, List<String> roleIds, List<String> userIds) {
        try {
            GetUsersForBulkMessageRequest.Builder req = GetUsersForBulkMessageRequest.newBuilder()
                    .setFetchAll(fetchAll);
            if (roleIds != null) {
                req.addAllRoleIds(roleIds);
            }
            if (userIds != null) {
                req.addAllUserIds(userIds);
            }
            GetUsersForBulkMessageResponse response = userProviderStub.getUsersForBulkMessage(req.build());
            List<UserContactDto> result = new ArrayList<>();
            for (UserContactInfo u : response.getUsersList()) {
                result.add(UserContactDto.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .mobileNumber(u.getMobileNumber())
                        .fullName(u.getFullName())
                        .fcmTokens(u.getFcmTokensList() != null ? new ArrayList<>(u.getFcmTokensList()) : List.of())
                        .build());
            }
            return result;
        } catch (StatusRuntimeException e) {
            log.error("User gRPC getUsersForBulkMessage failed: {}", e.getStatus(), e);
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE
                    || e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                throw new GlobalException(
                        ErrorCodeEnum.SRV001.name(),
                        ErrorCodeEnum.SRV001.getValue(),
                        HttpStatus.SERVICE_UNAVAILABLE);
            }
            throw new GlobalException(
                    ErrorCodeEnum.SRV001.name(),
                    e.getStatus().getDescription() != null ? e.getStatus().getDescription() : ErrorCodeEnum.SRV001.getValue(),
                    HttpStatus.BAD_GATEWAY);
        }
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserContactDto {
        private String id;
        private String email;
        private String mobileNumber;
        private String fullName;
        private List<String> fcmTokens;
    }
}
