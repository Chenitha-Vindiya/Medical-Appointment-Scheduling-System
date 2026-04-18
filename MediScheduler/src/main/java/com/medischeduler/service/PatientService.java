package com.medischeduler.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.Patient;
import com.medischeduler.repository.AppointmentRepository;
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

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Map<String, Object> getDoctorPatientDetails(Long doctorId) {
        // 1. Fetch all appointments for this doctor
        List<Appointment> allDoctorAppts = appointmentRepository.findByDoctorId(doctorId);

        // 2. Group appointments by Patient
        Map<Patient, List<Appointment>> apptsByPatient = allDoctorAppts.stream()
                .collect(Collectors.groupingBy(Appointment::getPatient));

        List<Patient> patients = new ArrayList<>(apptsByPatient.keySet());

        // 3. Create Maps to hold the calculated data
        Map<Long, LocalDate> lastVisits = new HashMap<>();
        Map<Long, String> nextAppts = new HashMap<>();
        Map<Long, String> conditions = new HashMap<>();

        LocalDate today = LocalDate.now();

        for (Patient p : patients) {
            List<Appointment> appts = apptsByPatient.get(p);
            LocalDate lastVisit = null;
            LocalDate nextAppt = null;
            String condition = "Checkup"; // Default

            for (Appointment a : appts) {
                LocalDate appDate = a.getAppointmentDate();

                // Check Past Appointments
                if (appDate.isBefore(today) && !a.getStatus().equals("CANCELLED")) {
                    if (lastVisit == null || appDate.isAfter(lastVisit)) {
                        lastVisit = appDate;
                        condition = a.getReason(); // Use past reason as condition
                    }
                }
                // Check Future Appointments
                else if (!appDate.isBefore(today) && (a.getStatus().equals("PENDING") || a.getStatus().equals("CONFIRMED"))) {
                    if (nextAppt == null || appDate.isBefore(nextAppt)) {
                        nextAppt = appDate;
                        if (lastVisit == null) {
                            condition = a.getReason(); // Fallback if it's a new patient
                        }
                    }
                }
            }

            // Put data into Maps using Patient ID as the key
            lastVisits.put(p.getId(), lastVisit);
            conditions.put(p.getId(), condition);

            // Format next appointment text
            if (nextAppt == null) {
                nextAppts.put(p.getId(), "TBD");
            } else {
                long daysBetween = ChronoUnit.DAYS.between(today, nextAppt);
                if (daysBetween == 0) nextAppts.put(p.getId(), "Today");
                else if (daysBetween == 1) nextAppts.put(p.getId(), "Tomorrow");
                else if (daysBetween <= 7) nextAppts.put(p.getId(), "In " + daysBetween + " Days");
                else nextAppts.put(p.getId(), nextAppt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }
        }

        // 4. Bundle everything into a single Map to return to the controller
        Map<String, Object> result = new HashMap<>();
        result.put("patients", patients);
        result.put("lastVisits", lastVisits);
        result.put("nextAppts", nextAppts);
        result.put("conditions", conditions);

        return result;
    }

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