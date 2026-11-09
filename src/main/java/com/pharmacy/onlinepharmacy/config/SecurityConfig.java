package com.pharmacy.onlinepharmacy.config;

import com.pharmacy.onlinepharmacy.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Консоль БД (для вас, чтобы смотреть данные)
                        .requestMatchers("/h2-console/**").permitAll()

                        // 2. Регистрация и вход (доступны без пароля)
                        .requestMatchers("/auth/**").permitAll()
                        // Запрещаем прямой доступ к файлам по URL
                        .requestMatchers("/uploads/**").denyAll()

                        // 3. Просмотр каталога (GET запросы) доступен всем
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        //Для админа
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        // 4. Панель фармацевта — ТОЛЬКО для роли PHARMACIST
                        .requestMatchers("/pharmacist/**").hasAuthority("ROLE_PHARMACIST")

                        // 5. Всё остальное (корзина, заказы, профиль) — только для авторизованных
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}