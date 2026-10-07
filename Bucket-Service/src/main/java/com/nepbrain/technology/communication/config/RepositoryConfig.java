package com.nepbrain.technology.communication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/** JPA repositories for legacy communication entities still bundled in bucket-service. */
@Configuration
@EnableJpaRepositories(basePackages = "com.nepbrain.technology.communication.repository")
public class RepositoryConfig {
}
