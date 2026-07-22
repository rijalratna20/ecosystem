package com.primus.server.security;

import com.primus.auth.model.TokenClaims;
import com.primus.auth.validator.TokenValidator;
import com.primus.common.exception.AuthException;
import com.primus.common.logging.CorrelationContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that validates bearer tokens and populates the {@link CorrelationContext}
 * for every inbound request. Skipped for actuator endpoints.
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String AUTHORIZATION = "Authorization";

    private final TokenValidator tokenValidator;

    public AuthenticationFilter(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader("X-Correlation-ID");
        CorrelationContext.set(correlationId);

        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader != null) {
            try {
                TokenClaims claims = tokenValidator.validate(authHeader);
                CorrelationContext.setUser(claims.getSubject());
                request.setAttribute("tokenClaims", claims);
            } catch (AuthException e) {
                log.debug("Token validation failed: {}", e.getMessage());
                // Allow request to proceed; controllers will reject if auth is required
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            CorrelationContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/actuator");
    }
}
