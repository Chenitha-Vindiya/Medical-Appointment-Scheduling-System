package com.medischeduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for development (easier with XAMPP/MySQL)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/patient/register", "/css/**", "/js/**").permitAll() // Allow these
                        .anyRequest().permitAll() // Temporarily allow everything so you can see your site
                )
                .formLogin(form -> form.disable()) // Disable the default Spring Security login page
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}