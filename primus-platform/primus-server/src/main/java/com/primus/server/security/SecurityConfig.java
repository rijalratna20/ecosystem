package com.primus.server.security;

import com.primus.auth.validator.TokenValidator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the {@link AuthenticationFilter} for all API paths.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authFilterRegistration(
            TokenValidator tokenValidator) {
        FilterRegistrationBean<AuthenticationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new AuthenticationFilter(tokenValidator));
        reg.addUrlPatterns("/api/*");
        reg.setOrder(1);
        return reg;
    }
}
