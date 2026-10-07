package com.nepbrain.technology.auth.controller;

import com.nepbrain.technology.auth.payload.response.ServiceResponse;
import com.nepbrain.technology.auth.payload.response.ServiceResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("/hello")
    public ResponseEntity<ServiceResponse> hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ServiceResponseBuilder.buildSuccess("Hello, " + authentication.getName()));
    }

}
