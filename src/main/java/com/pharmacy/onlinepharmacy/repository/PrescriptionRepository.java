package com.pharmacy.onlinepharmacy.repository;

import com.pharmacy.onlinepharmacy.entity.Prescription;
import com.pharmacy.onlinepharmacy.entity.User;
import com.pharmacy.onlinepharmacy.enums.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findAllByUser(User user);
    List<Prescription> findAllByStatus(PrescriptionStatus status);
}