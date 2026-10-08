package com.nepbrain.technology.postjob.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.postjob.annotation.CustomPostjobRestController;
import com.nepbrain.technology.postjob.payload.request.PostJobRequest;
import com.nepbrain.technology.postjob.service.PostJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@CustomPostjobRestController
@RequiredArgsConstructor
@Tag(name = "PostJob")
public class PostJobController {

    private final PostJobService postJobService;

    @PostMapping("/save-post-job")
    @Operation(summary = "Save post job")
    public ResponseEntity<GlobalResponse> savePostJob(@RequestBody PostJobRequest request) {
        return ResponseEntity.ok().body(postJobService.savePostJob(request));
    }

    @GetMapping("/get-post-job")
    @Operation(summary = "Post job list")
    public ResponseEntity<GlobalResponse> getPostJob(@RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok().body(postJobService.getPostJob(customerId));
    }

    @PostMapping("/get-post-job-detail")
    @Operation(summary = "Post job detail")
    public ResponseEntity<GlobalResponse> getPostJobDetail(@RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok().body(postJobService.getPostJobDetail(body.get("id")));
    }

    @PutMapping("/post-job/{id}/status")
    @Operation(summary = "Change post job status")
    public ResponseEntity<GlobalResponse> changePostJobStatus(@PathVariable UUID id, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(postJobService.changePostJobStatus(id, status));
    }

    @PostMapping("/post-job-delete/{id}")
    @Operation(summary = "Mark post job inactive")
    public ResponseEntity<GlobalResponse> postJobDelete(@PathVariable UUID id) {
        return ResponseEntity.ok().body(postJobService.postJobDelete(id));
    }
}
