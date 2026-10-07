package com.nepbrain.technology.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.url}")
    private String serverURL;

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Jojolapa technology");
        contact.setEmail("support@nepbrain.technology");
        contact.setUrl("https://nepbrain.technology");

        return new OpenAPI().info(new Info().title("Bucket & Communication API")
                        .description("File storage (bucket) and messaging (email, OTP, templates, settings)")
                        .contact(contact))
                .servers(List.of(new Server().url(serverURL)));
    }
    @Bean
    public OpenApiCustomizer addHeadersGlobal() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem -> {
                    if (pathItem != null) {
                        pathItem.readOperations().forEach(operation -> {
                            if (operation != null) {
                                operation.addParametersItem(new HeaderParameter()
                                        .name("X-XSRF-TOKEN").description("A custom header for all requests")
                                        .required(true).schema(new StringSchema()._default("BquLOJXXt2ng415MpvK4a8F0CF/w/1iawsnFqHzPGeo=")));
                            }
                        });
                    }
                });
            }
        };
    }
}
