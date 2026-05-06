package com.medischeduler.service;

import com.medischeduler.model.Doctor;
import com.medischeduler.model.Patient;
import com.medischeduler.model.WorkingHours;
import com.medischeduler.repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerDoctor(Doctor doctor) {
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

        // Set Default Hours: Mon-Fri (8-5) active, Weekends inactive
        List<WorkingHours> defaultHours = new ArrayList<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (String day : days) {
            boolean isWeekend = List.of("Saturday", "Sunday").contains(day);
            WorkingHours wh = WorkingHours.builder()
                    .day(day)
                    .startTime(java.time.LocalTime.of(8, 0))
                    .endTime(java.time.LocalTime.of(17, 0))
                    .active(!isWeekend) // Mon-Fri active
                    .doctor(doctor)
                    .build();
            defaultHours.add(wh);
        }
        doctor.setWorkingHours(defaultHours);

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

    public void updateDoctorWorkingHours(Long doctorId, List<WorkingHours> newHours) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

        if (doctor != null) {
            // Clear existing hours to avoid duplicates or orphans
            doctor.getWorkingHours().clear();

            if (newHours != null) {
                for (WorkingHours wh : newHours) {
                    // IMPORTANT: Link each hour to the doctor to set the foreign key
                    wh.setDoctor(doctor);
                    doctor.getWorkingHours().add(wh);
                }
            }
            // Save the doctor; CascadeType.ALL will save the hours automatically
            doctorRepository.save(doctor);
        }
    }

}
