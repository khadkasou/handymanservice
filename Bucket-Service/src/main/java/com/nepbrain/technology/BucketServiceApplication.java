package com.nepbrain.technology;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(
        excludeName = {
                "org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration",
                "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
        }
)
@RestController
@OpenAPIDefinition(info =
@Info(title = "Bucket API", version = "1.0", description = "File storage APIs")
)
public class BucketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BucketServiceApplication.class, args);
    }

    @GetMapping("/")
    public String welcome() {
        return "Bucket Service UP";
    }
}
