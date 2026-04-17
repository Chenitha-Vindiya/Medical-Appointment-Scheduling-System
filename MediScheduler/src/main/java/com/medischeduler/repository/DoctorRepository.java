package com.medischeduler.repository;

import com.medischeduler.model.Doctor;
import com.medischeduler.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByEmail(String email);
    Doctor findByNationalId(String nationalId);
    Doctor findByPhoneNumber(String phoneNumber);
    List<Doctor> findByActiveTrue();
}