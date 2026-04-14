package com.medischeduler.service;

import java.util.ArrayList;
import java.util.List;
import com.medischeduler.model.Patient;
import com.medischeduler.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerPatient(Patient patient) throws Exception {
        List<String> errors = new ArrayList<>();

        if (patientRepository.findByEmail(patient.getEmail()) != null) {
            errors.add("Email address is already registered.");
        }

        if (patientRepository.findByNationalId(patient.getNationalId()) != null) {
            errors.add("National ID (NIC) is already registered.");
        }

        //Phone Number (The new fix)
        if (patientRepository.findByPhoneNumber(patient.getPhoneNumber()) != null) {
            errors.add("Phone number is already registered.");
        }

        if (!errors.isEmpty()) {
            // Join errors with a delimiter or handle as a custom exception
            throw new Exception(String.join("|", errors));
        }

        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patientRepository.save(patient);
    }


    public Patient authenticate(String email, String password) {
        Patient patient = patientRepository.findByEmail(email);
        // Verify existence and password match in XAMPP (Exact logic from your file)
        if (patient != null && passwordEncoder.matches(password, patient.getPassword())) {
            return patient;
        }
        return null;
    }

    public boolean deactivateAccount(Long id) {
        Patient existingPatient = patientRepository.findById(id).orElse(null);
        if (existingPatient != null) {
            existingPatient.setActive(false);
            patientRepository.save(existingPatient);
            return true;
        }
        return false;
    }

    public Patient updateProfile(Patient formPatient) {
        Patient existingPatient = patientRepository.findById(formPatient.getId()).orElse(null);
        if (existingPatient != null) {
            existingPatient.setFirstName(formPatient.getFirstName());
            existingPatient.setLastName(formPatient.getLastName());
            existingPatient.setDateOfBirth(formPatient.getDateOfBirth());
            existingPatient.setGender(formPatient.getGender());
            existingPatient.setNationalId(formPatient.getNationalId());
            existingPatient.setEmail(formPatient.getEmail());
            existingPatient.setPhoneNumber(formPatient.getPhoneNumber());
            existingPatient.setHomeAddress(formPatient.getHomeAddress());
            existingPatient.setEmergencyContactName(formPatient.getEmergencyContactName());
            existingPatient.setRelationship(formPatient.getRelationship());
            existingPatient.setEmergencyPhone(formPatient.getEmergencyPhone());

            return patientRepository.save(existingPatient);
        }
        return null;
    }

    public String changePassword(Long id, String currentPassword, String newPassword) {
        Patient existingPatient = patientRepository.findById(id).orElse(null);

        if (existingPatient != null && passwordEncoder.matches(currentPassword, existingPatient.getPassword())) {
            existingPatient.setPassword(passwordEncoder.encode(newPassword));
            patientRepository.save(existingPatient);
            return "SUCCESS";
        }
        return "INVALID_CURRENT";
    }
}