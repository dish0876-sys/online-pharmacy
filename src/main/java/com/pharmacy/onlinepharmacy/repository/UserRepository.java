package com.pharmacy.onlinepharmacy.repository;

import com.pharmacy.onlinepharmacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring сам поймет, что нужно сделать SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}