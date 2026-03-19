package com.medischeduler.repository;

import com.medischeduler.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Spring will automatically create a query to find patient by email
    Patient findByEmail(String email);
}