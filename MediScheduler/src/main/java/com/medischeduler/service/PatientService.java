package com.medischeduler.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    /**
     * Fetches a specific patient's details and calculates their
     * last and next visit for the modal overview.
     */
    public Map<String, Object> getPatientModalDetails(Long patientId, Long doctorId) {
        Map<String, Object> response = new HashMap<>();

        // 1. Fetch Patient
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return response;
        }

        // 2. Fetch all appointments for THIS doctor and THIS patient
        List<Appointment> appts = appointmentRepository.findByPatientId(patientId).stream()
                .filter(a -> a.getDoctor().getId().equals(doctorId))
                .collect(Collectors.toList());

        Appointment lastVisit = null;
        Appointment nextVisit = null;
        LocalDateTime now = LocalDateTime.now();

        for (Appointment a : appts) {
            LocalDateTime appDateTime = LocalDateTime.of(a.getAppointmentDate(), a.getStartTime());

            // Find Last Visit
            if (appDateTime.isBefore(now) || a.getStatus().equalsIgnoreCase("COMPLETED")) {
                if (lastVisit == null || !appDateTime.isBefore(LocalDateTime.of(lastVisit.getAppointmentDate(), lastVisit.getStartTime()))) {
                    lastVisit = a;
                }
            }
            // Find Next Visit
            else if (!appDateTime.isBefore(now) && (a.getStatus().equalsIgnoreCase("PENDING") || a.getStatus().equalsIgnoreCase("CONFIRMED"))) {
                if (nextVisit == null || appDateTime.isBefore(LocalDateTime.of(nextVisit.getAppointmentDate(), nextVisit.getStartTime()))) {
                    nextVisit = a;
                }
            }
        }

        // --- THE FIX: Convert raw Entities to safe Maps to prevent JSON Serialization crashes ---

        Map<String, String> safeLastVisit = null;
        if (lastVisit != null) {
            safeLastVisit = new HashMap<>();
            safeLastVisit.put("appointmentDate", lastVisit.getAppointmentDate().toString());
            safeLastVisit.put("startTime", lastVisit.getStartTime().toString());
            safeLastVisit.put("location", lastVisit.getLocation());
            safeLastVisit.put("reason", lastVisit.getReason());
            safeLastVisit.put("status", lastVisit.getStatus());
        }

        Map<String, String> safeNextVisit = null;
        if (nextVisit != null) {
            safeNextVisit = new HashMap<>();
            safeNextVisit.put("appointmentDate", nextVisit.getAppointmentDate().toString());
            safeNextVisit.put("startTime", nextVisit.getStartTime().toString());
            safeNextVisit.put("location", nextVisit.getLocation());
            safeNextVisit.put("reason", nextVisit.getReason());
            safeNextVisit.put("status", nextVisit.getStatus());
        }

        // 3. Populate JSON Response Map
        response.put("patient", patient);
        response.put("lastVisit", safeLastVisit);
        response.put("nextVisit", safeNextVisit);

        return response;
    }

    public Map<String, Object> getDoctorPatientDetails(Long doctorId) {
        // 1. Fetch appointments and FILTER OUT all cancelled ones immediately
        List<Appointment> validDoctorAppts = appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .filter(app -> !app.getStatus().equalsIgnoreCase("CANCELLED"))
                .collect(Collectors.toList());

        // 2. Group the remaining valid appointments by Patient
        Map<Patient, List<Appointment>> apptsByPatient = validDoctorAppts.stream()
                .collect(Collectors.groupingBy(Appointment::getPatient));

        List<Patient> patients = new ArrayList<>(apptsByPatient.keySet());

        // 3. Create Maps to hold the calculated data
        Map<Long, LocalDate> lastVisits = new HashMap<>();
        Map<Long, String> nextAppts = new HashMap<>();
        Map<Long, String> conditions = new HashMap<>();

        // USE LOCAL DATETIME TO TRACK THE EXACT MINUTE
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        for (Patient p : patients) {
            List<Appointment> appts = apptsByPatient.get(p);
            LocalDate lastVisit = null;
            LocalDate nextAppt = null;
            String condition = "Checkup";

            for (Appointment a : appts) {
                // Always skip cancelled appointments regardless of time
                if (a.getStatus().equalsIgnoreCase("CANCELLED")) continue;

                LocalDate appDate = a.getAppointmentDate();
                LocalDateTime appDateTime = LocalDateTime.of(appDate, a.getStartTime());

                // 1. PAST APPOINTMENTS: (Is past OR is Completed) AND NOT Cancelled
                if (appDateTime.isBefore(now) || a.getStatus().equalsIgnoreCase("COMPLETED")) {
                    if (lastVisit == null || !appDate.isBefore(lastVisit)) {
                        lastVisit = appDate;
                        condition = a.getReason();
                    }
                }
                // 2. FUTURE APPOINTMENTS: (Is in future) AND (Pending/Confirmed)
                else if (appDateTime.isAfter(now) &&
                        (a.getStatus().equalsIgnoreCase("PENDING") || a.getStatus().equalsIgnoreCase("CONFIRMED"))) {

                    if (nextAppt == null || appDate.isBefore(nextAppt)) {
                        nextAppt = appDate;
                        if (lastVisit == null) {
                            condition = a.getReason();
                        }
                    }
                }
            }

            // Put Last Visit into Map
            lastVisits.put(p.getId(), lastVisit);

            // Format next appointment text AND override condition if necessary
            if (nextAppt == null) {
                nextAppts.put(p.getId(), "TBD");
                condition = "-"; // Override condition if no upcoming appointments
            } else {
                long daysBetween = ChronoUnit.DAYS.between(today, nextAppt);
                if (daysBetween == 0) nextAppts.put(p.getId(), "Today");
                else if (daysBetween == 1) nextAppts.put(p.getId(), "Tomorrow");
                else if (daysBetween <= 7) nextAppts.put(p.getId(), "In " + daysBetween + " Days");
                else nextAppts.put(p.getId(), nextAppt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }

            // Put Condition into Map AFTER the check
            conditions.put(p.getId(), condition);
        }

        // Sort: Active patients first, then alphabetically by first name
        patients.sort(Comparator.comparing(Patient::isActive).reversed().thenComparing(Patient::getFirstName));

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