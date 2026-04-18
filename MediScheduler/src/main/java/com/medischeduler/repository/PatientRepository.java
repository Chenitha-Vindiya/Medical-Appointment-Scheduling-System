package com.medischeduler.repository;

import com.medischeduler.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByEmail(String email);
    Patient findByNationalId(String nationalId);
    Patient findByPhoneNumber(String phoneNumber);
}