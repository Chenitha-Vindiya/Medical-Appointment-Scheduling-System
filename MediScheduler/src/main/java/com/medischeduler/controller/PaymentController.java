package com.medischeduler.controller;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.Doctor;
import com.medischeduler.model.Patient;
import com.medischeduler.model.Payment;
import com.medischeduler.repository.AppointmentRepository;
import com.medischeduler.repository.PaymentRepository;
import com.medischeduler.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @PostMapping("/patient/payment/submit-reference")
    public String submitPaymentReference(
            @RequestParam Long paymentId,
            @RequestParam String referenceNumber,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) {
            return "redirect:/login";
        }

        try {
            // Attempt to update the reference and flip status to PROCESSING
            paymentService.submitReference(paymentId, referenceNumber);

            // Pass success message to the glass-toast notification
            redirectAttributes.addFlashAttribute("success", "Payment reference submitted successfully. Awaiting verification.");
            return "redirect:/patient/payment?success=true";

        } catch (IllegalArgumentException e) {
            // Catches "Reference number is too short" or other business logic errors
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/payment?error=true";

        } catch (Exception e) {
            // Catches unexpected database or system errors
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred. Please try again.");
            return "redirect:/patient/payment?error=system";
        }
    }

    @PostMapping("/doctor/payment/verify")
    public String verifyPaymentReference(
            @RequestParam Long paymentId,
            @RequestParam String action,
            RedirectAttributes redirectAttributes) {

        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment invoice not found."));

            if ("APPROVE".equalsIgnoreCase(action)) {
                payment.setStatus("PAID");
                paymentRepository.save(payment);

                // Keep the associated appointment perfectly mapped
                if (payment.getAppointment() != null) {
                    Appointment app = payment.getAppointment();
                    // Optionally mark appointment as CONFIRMED if it was waiting on payment
                    if ("PENDING".equalsIgnoreCase(app.getStatus())) {
                        app.setStatus("CONFIRMED");
                        appointmentRepository.save(app);
                    }
                }
                redirectAttributes.addFlashAttribute("success", "Transaction verified successfully. Status mapped to PAID.");
            } else if ("REJECT".equalsIgnoreCase(action)) {
                // Roll back the processing parameter to let the patient submit a fresh string
                payment.setStatus("NOT PAID");
                payment.setReferenceNumber(null);
                paymentRepository.save(payment);
                redirectAttributes.addFlashAttribute("error", "Reference ID rejected. Patient has been notified to re-submit.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred processing the verification.");
        }

        return "redirect:/doctor/payment";
    }

    @PostMapping("/doctor/payment/create-cash-invoice")
    public String createCashInvoice(
            @RequestParam Long appointmentId,
            @RequestParam Double amount,
            RedirectAttributes redirectAttributes) {

        try {
            // Cleanly delegate database operations to the transactional service layer
            paymentService.createCashInvoice(appointmentId, amount);

            redirectAttributes.addFlashAttribute("success",
                    "Cash receipt generated successfully. Consultation marked as COMPLETED.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Catch business logic validations (e.g., duplicate invoices, invalid amounts)
            redirectAttributes.addFlashAttribute("error", e.getMessage());

        } catch (Exception e) {
            // Catch unexpected database errors
            redirectAttributes.addFlashAttribute("error",
                    "An unexpected error occurred while generating the invoice. Please try again.");
        }

        // Redirect back to the active payments dashboard to refresh the UI grids
        return "redirect:/doctor/payment";
    }
}
