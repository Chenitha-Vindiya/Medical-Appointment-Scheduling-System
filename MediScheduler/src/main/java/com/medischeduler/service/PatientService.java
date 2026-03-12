package com.medischeduler.service;

import com.medischeduler.model.Patient;
import com.medischeduler.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Patient registerPatient(Patient patient) {
        // Encrypt password before saving
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        return patientRepository.save(patient);
    }

    /**
     * Returns the patient or throws if not found.
     * Controllers should catch NoSuchElementException and handle appropriately.
     */
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "No patient found with email: " + email));
    }

    /**
     * Validates login credentials. Returns the patient on success, null on failure.
     */
    public Patient authenticate(String email, String rawPassword) {
        return patientRepository.findByEmail(email)
                .filter(p -> passwordEncoder.matches(rawPassword, p.getPassword()))
                .orElse(null);
    }
}