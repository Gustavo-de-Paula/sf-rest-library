package com.gustavo.sfrestlibrary.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!test")
public class SpringSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers("/v1/api-docs**", "/swagger-ui/**", "/swagger-ui.html")
                .permitAll()
                .anyRequest().authenticated()
        ).oauth2ResourceServer((oauth2) -> oauth2
                .jwt(Customizer.withDefaults()));

        return http.build();
    }
}
