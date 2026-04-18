package com.medischeduler.service;

import com.medischeduler.model.Appointment;
import com.medischeduler.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
        // Calculate the threshold time
        LocalTime timeLimit = LocalTime.now().plusMinutes(15);
        LocalDate today = LocalDate.now();

        // Find all pending appointments that are happening today
        // and have not been updated before the 15-minute mark
        List<Appointment> pending = repository.findByStatusAndAppointmentDateAndStartTimeBefore(
                "PENDING",
                today,
                timeLimit
        );

        if (!pending.isEmpty()) {
            for (Appointment app : pending) {
                app.setStatus("CANCELLED");
                repository.save(app);
            }
            System.out.println("Auto-cancelled " + pending.size() + " appointments.");
        }
    }
}