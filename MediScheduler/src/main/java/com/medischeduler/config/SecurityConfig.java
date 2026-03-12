package com.medischeduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Security configuration.
 *
 * FIX: Provides the BCryptPasswordEncoder bean required by PatientService
 * for hashing passwords before storage and verifying them on login.
 *
 * Place this file at: src/main/java/com/medischeduler/config/SecurityConfig.java
 */
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}