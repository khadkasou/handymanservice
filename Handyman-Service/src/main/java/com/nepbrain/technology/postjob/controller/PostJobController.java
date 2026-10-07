package com.nepbrain.technology.postjob.controller;
import com.nepbrain.technology.postjob.annotation.CustomPostjobRestController;
import com.nepbrain.technology.postjob.payload.request.PostJobRequest;
import com.nepbrain.technology.postjob.service.PostJobService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.Map; import java.util.UUID;

@CustomPostjobRestController @RequiredArgsConstructor
@Tag(name = "PostJob", description = "Post job APIs")
public class PostJobController {
    private final PostJobService postJobService;
    @PostMapping("/save-post-job") public ResponseEntity<GlobalResponse> savePostJob(@RequestBody PostJobRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SAVE_POST_JOB", postJobService.savePostJob(request)));
    }
    @GetMapping("/get-post-job") public ResponseEntity<GlobalResponse> getPostJob(@RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("GET_POST_JOB", postJobService.getPostJob(customerId)));
    }
    @PostMapping("/get-post-job-detail") public ResponseEntity<GlobalResponse> getPostJobDetail(@RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("GET_POST_JOB_DETAIL", postJobService.getPostJobDetail(body.get("id"))));
    }
    @PostMapping("/post-job-delete/{id}") public ResponseEntity<GlobalResponse> postJobDelete(@PathVariable UUID id) {
        postJobService.postJobDelete(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("POST_JOB_DELETE", "Post job deleted"));
    }
}
