package com.nepbrain.technology.bucket.grpc;

import com.nepbrain.technology.bucket.payload.request.BucketRequest;
import com.nepbrain.technology.bucket.service.BucketService;
import com.nepbrain.technology.core.exception.GlobalException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.StringUtils;

/**
 * gRPC server for bucket upload. Uploads base64 image to storage and returns URL only.
 */
@GrpcService
@RequiredArgsConstructor
@Slf4j
public class BucketGrpcServiceImpl extends BucketGrpcServiceGrpc.BucketGrpcServiceImplBase {

    private static final String DATA_URL_PREFIX = "data:";
    private static final String BASE64_PREFIX = "base64,";

    private final BucketService bucketService;

    @Override
    public void uploadImage(UploadImageRequest request, StreamObserver<UploadImageResponse> responseObserver) {
        try {
            String encodedData = request.getEncodedData();
            if (!StringUtils.hasText(encodedData)) {
                responseObserver.onNext(UploadImageResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("encoded_data is required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            // Strip data URL prefix if present (e.g. "data:image/png;base64,...")
            String rawBase64 = encodedData;
            if (encodedData.toLowerCase().startsWith(DATA_URL_PREFIX)) {
                int base64Index = encodedData.indexOf(BASE64_PREFIX);
                if (base64Index >= 0) {
                    rawBase64 = encodedData.substring(base64Index + BASE64_PREFIX.length()).trim();
                }
            }
            String folderPath = StringUtils.hasText(request.getFolderPath()) ? request.getFolderPath() : "master";
            String fileName = StringUtils.hasText(request.getFileName()) ? request.getFileName() : "image";

            BucketRequest bucketRequest = BucketRequest.builder()
                    .encodedData(rawBase64)
                    .folderPath(folderPath)
                    .fileName(fileName)
                    .build();
            String url = bucketService.uploadImageAndGetUrl(bucketRequest);
            responseObserver.onNext(UploadImageResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Uploaded successfully")
                    .setUrl(url)
                    .build());
        } catch (GlobalException e) {
            log.warn("Bucket gRPC upload failed: {}", e.getMessage());
            responseObserver.onNext(UploadImageResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage() != null ? e.getMessage() : "BUK001")
                    .build());
        } catch (Exception e) {
            log.error("Bucket gRPC upload error", e);
            responseObserver.onNext(UploadImageResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Upload failed: " + e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void uploadUserPhoto(UploadUserPhotoRequest request, StreamObserver<UploadImageResponse> responseObserver) {
        try {
            String userId = request.getUserId();
            if (!StringUtils.hasText(userId)) {
                responseObserver.onNext(UploadImageResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("user_id is required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            String encodedData = request.getEncodedData();
            if (!StringUtils.hasText(encodedData)) {
                responseObserver.onNext(UploadImageResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("encoded_data is required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            String rawBase64 = encodedData;
            if (encodedData.toLowerCase().startsWith(DATA_URL_PREFIX)) {
                int base64Index = encodedData.indexOf(BASE64_PREFIX);
                if (base64Index >= 0) {
                    rawBase64 = encodedData.substring(base64Index + BASE64_PREFIX.length()).trim();
                }
            }
            // Store under user/{user_id}/ e.g. user/550e8400-e29b-41d4-a716-446655440000
            String folderPath = "user" + "/" + userId.trim();
            String fileName = "photo";

            BucketRequest bucketRequest = BucketRequest.builder()
                    .encodedData(rawBase64)
                    .folderPath(folderPath)
                    .fileName(fileName)
                    .build();
            String url = bucketService.uploadImageAndGetUrl(bucketRequest);
            responseObserver.onNext(UploadImageResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Uploaded successfully")
                    .setUrl(url)
                    .build());
        } catch (GlobalException e) {
            log.warn("Bucket gRPC upload user photo failed: {}", e.getMessage());
            responseObserver.onNext(UploadImageResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage() != null ? e.getMessage() : "BUK001")
                    .build());
        } catch (Exception e) {
            log.error("Bucket gRPC upload user photo error", e);
            responseObserver.onNext(UploadImageResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Upload failed: " + e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }
}
