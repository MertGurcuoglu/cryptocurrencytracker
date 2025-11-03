package com.mertgurcuoglu.api_gateway.controller;

import com.mertgurcuoglu.api_gateway.config.GoogleVerifier;
import com.mertgurcuoglu.api_gateway.config.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final GoogleVerifier googleVerifier;

    public AuthController(JwtUtil jwtUtil, GoogleVerifier googleVerifier) {
        this.jwtUtil = jwtUtil;
        this.googleVerifier = googleVerifier;
    }

    @PostMapping("/google")
    public Mono<ResponseEntity<?>> googleLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("token");

        if (idToken == null) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Google token eksik")));
        }

        try {
            String email = googleVerifier.verify(idToken);
            
            if (email != null) {
                String role = "mrthkk23@gmail.com".equalsIgnoreCase(email) ? "ROLE_ADMIN" : "ROLE_USER";
                String jwt = jwtUtil.generateToken(email, role);
                
                return Mono.just(ResponseEntity.ok(Map.of("jwt", jwt)));
            } else {
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Geçersiz Google token")));
            }
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Token doğrulaması sırasında sunucu hatası")));
        }
    }
}
