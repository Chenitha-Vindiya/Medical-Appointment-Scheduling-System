package com.medischeduler.service;

import com.medischeduler.model.*;
import com.medischeduler.repository.AppointmentRepository;
import com.medischeduler.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // --- VALIDATION LOGIC ---
    private List<String> validateAppointment(Long doctorId, LocalDate date, LocalTime time, Long currentAppId) {
        List<String> errors = new ArrayList<>();

        // 0. Date & Time Validation
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (date.isBefore(today)) {
            errors.add("You cannot book an appointment for a past date.");
        } else if (date.isEqual(today)) {
            if (time.isBefore(now)) {
                errors.add("The selected time has already passed for today.");
            } else if (time.isBefore(now.plusMinutes(30))) {
                errors.add("Appointments must be booked at least 30 minutes in advance.");
            }
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!doctor.isActive()) {
            errors.add("This doctor is currently not accepting appointments.");
        }

        if (time.getMinute() != 0 && time.getMinute() != 30) {
            errors.add("Invalid time slot. Please select a time ending in :00 or :30.");
        }

        String dayOfWeekRaw = date.getDayOfWeek().name();
        String dayFormatted = dayOfWeekRaw.substring(0, 1).toUpperCase() + dayOfWeekRaw.substring(1).toLowerCase();

        boolean doctorWorksThisDay = false;
        boolean timeIsWithinShift = false;

        for (WorkingHours wh : doctor.getWorkingHours()) {
            if (wh.isActive() && wh.getDay().equalsIgnoreCase(dayOfWeekRaw)) {
                doctorWorksThisDay = true;
                LocalTime appointmentEndTime = time.plusMinutes(30);
                if (!time.isBefore(wh.getStartTime()) && !appointmentEndTime.isAfter(wh.getEndTime())) {
                    timeIsWithinShift = true;
                    break;
                }
            }
        }

        if (!doctorWorksThisDay) {
            errors.add("Doctor does not work on " + dayFormatted + "s.");
        } else if (!timeIsWithinShift) {
            errors.add("The selected time is outside the doctor's working hours for " + dayFormatted + ".");
        }

        // Overlap Check (Ignore the current appointment ID if we are rescheduling)
        List<Appointment> existing = appointmentRepository.findByAppointmentDateAndStartTime(date, time);
        boolean isOccupied = existing.stream()
                .filter(app -> !app.getId().equals(currentAppId)) // Critical: Don't collide with yourself
                .anyMatch(app -> app.getDoctor().getId().equals(doctorId));

        if (isOccupied) {
            errors.add("This time slot is already booked.");
        }

        return errors;
    }

    // --- CREATE ---
    public List<String> createAppointment(Long doctorId, String reason, LocalDate date, LocalTime time, Patient patient) {
        List<String> errors = validateAppointment(doctorId, date, time, null);

        if (errors.isEmpty()) {
            Appointment appointment = new Appointment();
            appointment.setDoctor(doctorRepository.findById(doctorId).get());
            appointment.setPatient(patient);
            appointment.setReason(reason);
            appointment.setAppointmentDate(date);
            appointment.setStartTime(time);
            appointment.setStatus("PENDING");
            appointmentRepository.save(appointment);
        }
        return errors;
    }

    // --- RESCHEDULE ---
    public List<String> rescheduleAppointment(Long appointmentId, LocalDate date, LocalTime time, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Validate using the existing doctor, but new date/time
        List<String> errors = validateAppointment(appointment.getDoctor().getId(), date, time, appointmentId);

        if (errors.isEmpty()) {
            appointment.setAppointmentDate(date);
            appointment.setStartTime(time);
            appointment.setReason(reason);
            appointment.setStatus("PENDING"); // Reset status so doctor can see change
            appointmentRepository.save(appointment);
        }
        return errors;
    }
}