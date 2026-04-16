package com.medischeduler.service;

import com.medischeduler.model.Doctor;
import com.medischeduler.model.Patient;
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

    public Doctor updateProfile(Doctor formDoctor) {
        //Fetch the existing doctor from the DB by ID
        Doctor existingDoctor = doctorRepository.findById(formDoctor.getId()).orElse(null);

        if (existingDoctor != null) {
            // Map personal info from the form
            existingDoctor.setFirstName(formDoctor.getFirstName());
            existingDoctor.setLastName(formDoctor.getLastName());
            existingDoctor.setPhoneNumber(formDoctor.getPhoneNumber());
            existingDoctor.setEmail(formDoctor.getEmail());
            existingDoctor.setNationalId(formDoctor.getNationalId());

            // Map professional info from the form
            existingDoctor.setSpecialization(formDoctor.getSpecialization());
            existingDoctor.setDepartment(formDoctor.getDepartment());
            existingDoctor.setConsultationFees(formDoctor.getConsultationFees());

            // 3. Save the updated doctor back to MySQL
            return doctorRepository.save(existingDoctor);
        }
        return null;
    }

    public boolean deactivateAccount(Long id) {
        Doctor existingDoctor = doctorRepository.findById(id).orElse(null);
        if (existingDoctor != null) {
            existingDoctor.setActive(false);
            doctorRepository.save(existingDoctor);
            return true;
        }
        return false;
    }

    public String changePassword(Long id, String currentPassword, String newPassword) {
        Doctor existingDoctor = doctorRepository.findById(id).orElse(null);

        if (existingDoctor != null && passwordEncoder.matches(currentPassword, existingDoctor.getPassword())) {
            existingDoctor.setPassword(passwordEncoder.encode(newPassword));
            doctorRepository.save(existingDoctor);
            return "SUCCESS";
        }
        return "INVALID_CURRENT";
    }
}