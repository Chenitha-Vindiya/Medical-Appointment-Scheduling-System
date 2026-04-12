package com.medischeduler.repository;

import com.medischeduler.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Find by email AND ensure the account is active
    Patient findByEmailAndActiveTrue(String email);

    Patient findByEmail(String email);
}