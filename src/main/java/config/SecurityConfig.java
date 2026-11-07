package com.pharmacy.onlinepharmacy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Отключаем CSRF-защиту (временная мера для удобной разработки)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Настраиваем правила доступа к URL
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем всем заходить в консоль базы данных H2
                        .requestMatchers("/h2-console/**").permitAll()
                        // Временно разрешаем все остальные запросы, чтобы не мешать разработке API
                        .anyRequest().permitAll()
                )

                // 3. Разрешаем отображение консоли H2 во фреймах (иначе будет белый экран)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );

        return http.build();
    }

    // Этот бин понадобится нам позже для шифрования паролей пользователей
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}