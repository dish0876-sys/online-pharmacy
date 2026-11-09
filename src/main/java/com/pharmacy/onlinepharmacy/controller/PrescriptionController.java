package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.entity.Prescription;
import com.pharmacy.onlinepharmacy.entity.User;
import com.pharmacy.onlinepharmacy.enums.PrescriptionStatus;
import com.pharmacy.onlinepharmacy.repository.PrescriptionRepository;
import com.pharmacy.onlinepharmacy.repository.UserRepository;
import com.pharmacy.onlinepharmacy.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<String> uploadPrescription(@RequestParam("file") MultipartFile file, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        String filePath = fileStorageService.saveFile(file);

        Prescription prescription = Prescription.builder()
                .user(user)
                .filePath(filePath)
                .status(PrescriptionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        prescriptionRepository.save(prescription);
        return ResponseEntity.ok("Рецепт загружен и отправлен на проверку");
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> getMyPrescriptions(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(prescriptionRepository.findAllByUser(user));
    }
}