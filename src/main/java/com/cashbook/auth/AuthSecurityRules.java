package com.cashbook.auth;

import com.cashbook.auth.common.SecurityRules;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class AuthSecurityRules implements SecurityRules {

    @Override
    public void config(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>
                    .AuthorizationManagerRequestMatcherRegistry registry) {

        // Public auth endpoints
        registry.requestMatchers("/auth/login").permitAll();
        registry.requestMatchers("/auth/refresh").permitAll();

        // Health check endpoint for Docker / Nginx / monitoring
        registry.requestMatchers("/actuator/health").permitAll();

        // Optional: allow all auth endpoints
        // registry.requestMatchers("/auth/**").permitAll();
    }
}
