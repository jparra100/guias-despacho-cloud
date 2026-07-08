package com.duocuc.sistemaguiasdespacho.config;

import com.duocuc.sistemaguiasdespacho.security.AzureB2CRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Habilita el backend como OAuth2 Resource Server: Spring Security valida
 * automaticamente la firma y expiracion de cada JWT emitido por Azure AD B2C
 * usando el issuer-uri / jwk-set-uri definido en application.yml
 * (propiedad spring.security.oauth2.resourceserver.jwt.*).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // habilita @PreAuthorize en los controllers
public class SecurityConfig {

    private final AzureB2CRoleConverter azureB2CRoleConverter;

    public SecurityConfig(AzureB2CRoleConverter azureB2CRoleConverter) {
        this.azureB2CRoleConverter = azureB2CRoleConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API REST sin estado, sin formularios
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(azureB2CRoleConverter))
            );

        return http.build();
    }
}
