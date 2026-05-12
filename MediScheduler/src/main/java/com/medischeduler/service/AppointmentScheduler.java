package com.medischeduler.service;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.Payment;
import com.medischeduler.repository.AppointmentRepository;
import com.medischeduler.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentScheduler {

    @Autowired
    private AppointmentRepository repository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private HistoryService historyService;

    /**
     * Automatically cancels PENDING appointments that are starting within the next 15 minutes.
     * Safely ignores appointments that already have submitted funds.
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
            List<Appointment> safeToCancel = new ArrayList<>();

            for (Appointment app : pending) {
                Optional<Payment> paymentOpt = paymentRepository.findByAppointmentId(app.getId());

                // Check if money is tied to this appointment
                boolean hasSecuredFunds = paymentOpt.isPresent() &&
                        ("PROCESSING".equalsIgnoreCase(paymentOpt.get().getStatus()) || "PAID".equalsIgnoreCase(paymentOpt.get().getStatus()));

                // Only cancel if no funds are processing
                if (!hasSecuredFunds) {
                    app.setStatus("CANCELLED");
                    historyService.createHistoryRecord(app);
                    safeToCancel.add(app);

                    // If an unpaid payment record exists, cancel it alongside the appointment
                    if (paymentOpt.isPresent()) {
                        Payment p = paymentOpt.get();
                        p.setStatus("CANCELLED");
                        paymentRepository.save(p);
                    }
                }
            }

            if (!safeToCancel.isEmpty()) {
                repository.saveAll(safeToCancel);
                System.out.println("Auto-cancelled " + safeToCancel.size() + " unpaid appointments.");
            }
        }
    }

    /**
     * Automatically completes CONFIRMED appointments after their duration has passed.
     * Safely auto-generates missing CASH invoices.
     * Runs every 60 seconds.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCompleteAppointments() {
        List<Appointment> confirmedAppts = repository.findByStatus("CONFIRMED");

        LocalDateTime now = LocalDateTime.now();
        List<Appointment> toComplete = new ArrayList<>();

        for (Appointment app : confirmedAppts) {
            LocalDateTime endTime = LocalDateTime.of(app.getAppointmentDate(), app.getStartTime())
                    .plusMinutes(Appointment.DURATION_MINUTES);

            if (endTime.isBefore(now)) {
                app.setStatus("COMPLETED");
                toComplete.add(app);
                historyService.createHistoryRecord(app);

                // ATOMIC CHECK: Safely auto-create CASH payment if missing
                if ("CASH".equalsIgnoreCase(app.getPaymentMethod())) {
                    Optional<Payment> existing = paymentRepository.findByAppointmentId(app.getId());
                    if (existing.isEmpty()) {
                        Payment cashPayment = new Payment();
                        cashPayment.setAppointment(app);
                        cashPayment.setPatient(app.getPatient());
                        cashPayment.setPaymentMethod("CASH");
                        cashPayment.setStatus("PAID");

                        // Inherit standard consultation amount mapping
                        Double fee = app.getDoctor() != null && app.getDoctor().getConsultationFees() != null
                                ? app.getDoctor().getConsultationFees() : 0.0;
                        cashPayment.setAmount(fee);

                        paymentRepository.save(cashPayment);
                    }
                }
            }
        }

        if (!toComplete.isEmpty()) {
            repository.saveAll(toComplete);
            System.out.println("Auto-completed " + toComplete.size() + " appointments.");
        }
    }
}