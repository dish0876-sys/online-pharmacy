package com.pharmacy.onlinepharmacy.service;

import com.pharmacy.onlinepharmacy.dto.RegisterRequest;
import com.pharmacy.onlinepharmacy.entity.User;
import com.pharmacy.onlinepharmacy.enums.Role;
import com.pharmacy.onlinepharmacy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    public void register(RegisterRequest request) {
        // Проверяем, нет ли уже такого email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Создаем нового пользователя
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Шифруем пароль!
                .role(Role.CLIENT) // По умолчанию все регистрируются как клиенты
                .isBlocked(false)
                .build();

        userRepository.save(user);
    }
}