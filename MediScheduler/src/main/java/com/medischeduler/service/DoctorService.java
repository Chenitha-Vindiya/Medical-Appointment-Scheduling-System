package com.medischeduler.service;

import com.medischeduler.model.Doctor;
import com.medischeduler.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerDoctor(Doctor doctor) throws Exception {
        List<String> errors = new ArrayList<>();

        if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
            errors.add("Email address is already registered.");
        }

        if (doctorRepository.findByNationalId(doctor.getNationalId()) != null) {
            errors.add("National ID (NIC) is already registered.");
        }

        //Phone Number (The new fix)
        if (doctorRepository.findByPhoneNumber(doctor.getPhoneNumber()) != null) {
            errors.add("Phone number is already registered.");
        }

        if (!errors.isEmpty()) {
            // Join errors with a delimiter or handle as a custom exception
            throw new Exception(String.join("|", errors));
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