package com.nepbrain.technology.communication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Explicit repository configuration so that with multiple Spring Data modules (JPA + Redis)
 * only JPA repositories are registered from the communication package, and Redis repository
 * scanning is limited to a dedicated package (no Redis repos used), avoiding "Could not safely
 * identify store assignment" warnings on startup.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.nepbrain.technology.communication.repository")
@EnableRedisRepositories(basePackages = "com.nepbrain.technology.communication.redis")
public class RepositoryConfig {
}
