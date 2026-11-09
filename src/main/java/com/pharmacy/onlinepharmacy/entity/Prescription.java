package com.pharmacy.onlinepharmacy.entity;

import com.pharmacy.onlinepharmacy.enums.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_path", nullable = false)
    private String filePath; // Путь к файлу на диске

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionStatus status;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "pharmacist_comment")
    private String pharmacistComment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}