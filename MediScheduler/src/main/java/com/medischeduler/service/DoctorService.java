package com.medischeduler.service;

import com.medischeduler.model.Doctor;
import com.medischeduler.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerDoctor(Doctor doctor) throws Exception {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
            throw new Exception("Email is already in use.");
        }
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        doctorRepository.save(doctor);
    }

    public Doctor authenticate(String email, String password) {
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor != null && passwordEncoder.matches(password, doctor.getPassword())) {
            return doctor;
        }
        return null;
    }
}