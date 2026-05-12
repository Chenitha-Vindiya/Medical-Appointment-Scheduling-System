package com.medischeduler.service;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.Payment;
import com.medischeduler.repository.AppointmentRepository;
import com.medischeduler.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public void submitReference(Long paymentId, String rawReferenceNumber) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment ID"));

        // Completely sanitize the input: remove leading/trailing whitespace
        String cleanRef = rawReferenceNumber.trim();

        // Optional basic backend safety check
        if (cleanRef.length() < 5) {
            throw new IllegalArgumentException("Reference number is too short.");
        }

        payment.setReferenceNumber(cleanRef);
        payment.setStatus("PROCESSING");

        paymentRepository.save(payment);
    }

    @Transactional
    public void createCashInvoice(Long appointmentId, Double amount) {
        // 1. Fetch the corresponding appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Target consultation not found."));

        // 2. Safety Check: Prevent generating duplicate invoices
        if (appointment.getPayment() != null) {
            throw new IllegalStateException("An invoice already exists for this consultation.");
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Invoice fee must be greater than zero.");
        }

        // 3. Build the finalized Cash Payment entity
        Payment cashInvoice = new Payment();
        cashInvoice.setAppointment(appointment);
        cashInvoice.setPatient(appointment.getPatient());
        cashInvoice.setAmount(amount);
        cashInvoice.setPaymentMethod("CASH");
        cashInvoice.setStatus("PAID"); // Cash invoices are generated at the point of physical collection

        // 4. Update the Appointment status to reflect the finalized physical visit
        appointment.setStatus("COMPLETED");

        // 5. Persist the state changes (Cascading saves the payment if configured, otherwise save explicitly)
        paymentRepository.save(cashInvoice);
        appointmentRepository.save(appointment);
    }
}
