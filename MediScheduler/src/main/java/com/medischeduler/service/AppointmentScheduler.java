package com.medischeduler.service;

import com.medischeduler.model.Appointment;
import com.medischeduler.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentScheduler {

    @Autowired
    private AppointmentRepository repository;

    /**
     * Automatically cancels PENDING appointments
     * that are starting within the next 15 minutes.
     * Runs every 60 seconds.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCancelAppointments() {
        LocalTime timeLimit = LocalTime.now().plusMinutes(15);
        LocalDate today = LocalDate.now();

        List<Appointment> pending = repository.findByStatusAndAppointmentDateAndStartTimeBefore(
                "PENDING",
                today,
                timeLimit
        );

        if (!pending.isEmpty()) {
            for (Appointment app : pending) {
                app.setStatus("CANCELLED");
            }
            repository.saveAll(pending); // saveAll is much faster for multiple records
            System.out.println("Auto-cancelled " + pending.size() + " appointments.");
        }
    }

    /**
     * Automatically completes CONFIRMED appointments
     * after their 30-minute duration has passed.
     * Runs every 60 seconds.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCompleteAppointments() {
        // 1. Fetch all currently CONFIRMED appointments
        List<Appointment> confirmedAppts = repository.findByStatus("CONFIRMED");

        LocalDateTime now = LocalDateTime.now();
        List<Appointment> toComplete = new ArrayList<>();

        for (Appointment app : confirmedAppts) {
            // 2. Combine Date and Time, then add the 30-minute duration to find the exact End Time
            LocalDateTime endTime = LocalDateTime.of(app.getAppointmentDate(), app.getStartTime())
                    .plusMinutes(Appointment.DURATION_MINUTES);

            // 3. If the exact end time has passed, mark it for completion
            if (endTime.isBefore(now)) {
                app.setStatus("COMPLETED");
                toComplete.add(app);
            }
        }

        // 4. Save all updated appointments to the database at once
        if (!toComplete.isEmpty()) {
            repository.saveAll(toComplete);
            System.out.println("Auto-completed " + toComplete.size() + " appointments.");
        }
    }
}