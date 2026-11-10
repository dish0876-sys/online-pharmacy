package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.dto.AuthResponse;
import com.pharmacy.onlinepharmacy.dto.RegisterRequest;
import com.pharmacy.onlinepharmacy.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody RegisterRequest request,
                                              HttpServletRequest httpRequest,
                                              HttpServletResponse httpResponse) {
        // 1. Проверяем логин и пароль
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Создаем контекст безопасности и сохраняем его в СЕССИЮ (создается JSESSIONID)
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Этот метод сохраняет контекст в HttpSession
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        // 3. Возвращаем ответ
        return ResponseEntity.ok(new AuthResponse(
                "Успешный вход",
                authentication.getName(),
                authentication.getAuthorities().iterator().next().getAuthority()
        ));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // Очищаем контекст безопасности
        SecurityContextHolder.clearContext();
        // Инвалидируем (уничтожаем) сессию
        request.getSession().invalidate();
        return ResponseEntity.ok("Вы успешно вышли из системы");
    }
}