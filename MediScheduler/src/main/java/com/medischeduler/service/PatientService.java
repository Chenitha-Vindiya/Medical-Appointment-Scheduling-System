package com.medischeduler.service;

import com.medischeduler.model.Patient;
import com.medischeduler.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    public Patient registerPatient(Patient patient) {
        // In a real app, encrypt the password here before saving
        return patientRepository.save(patient);
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email).orElse(null);
    }
}