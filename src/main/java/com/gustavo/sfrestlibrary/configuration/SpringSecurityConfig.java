package com.gustavo.sfrestlibrary.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@Profile("!test")
public class SpringSecurityConfig {
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers(mvc.pattern("/v1/api-docs**"),
                        mvc.pattern("/swagger-ui/**"),
                        mvc.pattern("/swagger-ui.html"))
                .permitAll()
                .anyRequest().authenticated()
                .and().oauth2ResourceServer().jwt();
        return http.build();
    }
}
