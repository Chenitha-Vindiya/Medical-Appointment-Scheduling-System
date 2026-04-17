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

    public List<String> createAppointment(Long doctorId, String reason, LocalDate date, LocalTime time, Patient patient) {
        List<String> errors = new ArrayList<>();

        // 0. Strict Date & Time Validation
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            errors.add("You cannot book an appointment for a past date.");
        } else if (date.isEqual(today) && time.isBefore(LocalTime.now())) {
            errors.add("The selected time has already passed for today.");
        }else if (date.isEqual(today) && time.isBefore(LocalTime.now().plusMinutes(30))) {
            errors.add("Appointments must be booked at least 30 minutes in advance.");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // 1. Check Doctor Active
        if (!doctor.isActive()) {
            errors.add("This doctor is currently not accepting appointments.");
        }

        // 2. Validate Time Slot minutes
        int minutes = time.getMinute();
        if (minutes != 0 && minutes != 30) {
            errors.add("Invalid time slot. Please select a time ending in :00 or :30.");
        }

// 3. Validate against Doctor's Working Hours
        String dayOfWeekRaw = date.getDayOfWeek().name(); // e.g., "MONDAY"

// Convert "MONDAY" to "Monday" (First letter Capital, rest lowercase)
        String dayFormatted = dayOfWeekRaw.substring(0, 1).toUpperCase()
                + dayOfWeekRaw.substring(1).toLowerCase();

        boolean doctorWorksThisDay = false;
        boolean timeIsWithinShift = false;

        for (WorkingHours wh : doctor.getWorkingHours()) {
            if (wh.isActive() && wh.getDay().equalsIgnoreCase(dayOfWeekRaw)) {
                doctorWorksThisDay = true;

                // Calculate when this specific appointment would end
                LocalTime appointmentEndTime = time.plusMinutes(30);

                // Logic:
                // 1. Start time must not be before the doctor starts.
                // 2. Calculated end time must not be after the doctor ends.
                if (!time.isBefore(wh.getStartTime()) && !appointmentEndTime.isAfter(wh.getEndTime())) {
                    timeIsWithinShift = true;
                    break;
                }
            }
        }

        // Separate Error Messages with "Monday" format
        if (!doctorWorksThisDay) {
            errors.add("Doctor does not work on " + dayFormatted + "s.");
        } else if (!timeIsWithinShift) {
            errors.add("The selected time is outside the doctor's working hours for " + dayFormatted + ".");
        }

        // 4. Check Overlap
        List<Appointment> existing = appointmentRepository.findByAppointmentDateAndStartTime(date, time);
        boolean isOccupied = existing.stream().anyMatch(app -> app.getDoctor().getId().equals(doctorId));
        if (isOccupied) {
            errors.add("This time slot is already booked by another patient.");
        }

        // Only save if NO errors occurred
        if (errors.isEmpty()) {
            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            appointment.setReason(reason);
            appointment.setAppointmentDate(date);
            appointment.setStartTime(time);
            appointment.setStatus("PENDING");
            appointmentRepository.save(appointment);
        }

        return errors;
    }
}