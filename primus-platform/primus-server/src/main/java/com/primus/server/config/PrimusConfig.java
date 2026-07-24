package com.primus.server.config;

import com.primus.auth.validator.TokenValidator;
import com.primus.metadata.registry.MetadataRegistry;
import com.primus.nas.NasStorageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

/**
 * Core infrastructure bean configuration.
 */
@Configuration
public class PrimusConfig {

    @Bean
    public MetadataRegistry metadataRegistry() {
        return new MetadataRegistry();
    }

    @Bean
    public NasStorageProvider nasStorageProvider(
            @Value("${primus.storage.nas.root-path:/tmp/primus/storage}") String rootPath) {
        return new NasStorageProvider(Path.of(rootPath));
    }

    @Bean
    public TokenValidator tokenValidator(
            @Value("${primus.auth.issuer:primus}") String issuer,
            @Value("${primus.auth.clock-skew-seconds:30}") long clockSkewSeconds) {
        return new TokenValidator(issuer, clockSkewSeconds);
    }
}
