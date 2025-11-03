package com.mertgurcuoglu.api_gateway.config;

import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(authz -> authz
                .pathMatchers(HttpMethod.POST, "/api/currencies").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/api/currencies/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/api/currencies/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/api/auth/google").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
    
    @Bean
    public GlobalFilter customHeaderFilter() {
        
        return (exchange, chain) -> exchange.getPrincipal()
            .cast(Principal.class)
            .flatMap(principal -> {
                String username = principal.getName();
                var mutatedRequest = exchange.getRequest().mutate()
                        .header("X-Authenticated-User-Id", username)
                        .build();
                var mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                return chain.filter(mutatedExchange);
            })
            .switchIfEmpty(chain.filter(exchange));
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HMACSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

