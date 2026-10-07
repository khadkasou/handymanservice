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

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Jojolapa technology");
        contact.setEmail("support@nepbrain.technology");
        contact.setUrl("https://nepbrain.technology");

        return new OpenAPI().info(new Info().title(applicationName + " API")
                        .description(applicationName + " Project")
                        .contact(contact))
                .servers(List.of(new Server().url(serverURL)));
    }
    @Bean
    public OpenApiCustomizer addHeadersGlobal() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                operation.addParametersItem(new HeaderParameter()
                        .name("X-XSRF-TOKEN").description("A custom header for all requests")
                        .required(true).schema(new StringSchema()._default("BquLOJXXt2ng415MpvK4a8F0CF/w/1iawsnFqHzPGeo=")));
            }));
        };
    }
}
