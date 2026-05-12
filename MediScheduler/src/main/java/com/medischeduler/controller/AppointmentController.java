package com.medischeduler.controller;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.Patient;
import com.medischeduler.model.Payment;
import com.medischeduler.repository.AppointmentRepository;
import com.medischeduler.repository.PaymentRepository;
import com.medischeduler.service.AppointmentService;
import com.medischeduler.service.HistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/patient/appointment/create")
    public String createAppointment(
            @RequestParam Long doctorId,
            @RequestParam String reason,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam String paymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient == null) {
            return "redirect:/login";
        }

        List<String> errors = appointmentService.createAppointment(doctorId, reason, appointmentDate, startTime, patient, paymentMethod);

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorList", errors);
            return "redirect:/patient/appointment";
        }

        return "redirect:/patient/appointment?success=true";
    }

    @PostMapping("/patient/appointment/reschedule")
    public String rescheduleAppointment(
            @RequestParam Long appointmentId,
            @RequestParam String reason,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) {
            return "redirect:/login";
        }

        Appointment app = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        String previousStatus = app.getStatus();

        List<String> errors = appointmentService.rescheduleAppointment(
                appointmentId, appointmentDate, startTime, reason);

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorList", errors);
            redirectAttributes.addFlashAttribute("isRescheduleError", true);
            return "redirect:/patient/appointment?editError=true";
        }

        if ("CANCELLED".equalsIgnoreCase(previousStatus)) {
            Optional<Payment> paymentOpt = paymentRepository.findByAppointmentId(appointmentId);
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                payment.setStatus("NOT PAID");
                paymentRepository.save(payment);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Appointment updated successfully!");
        return "redirect:/patient/appointment?rescheduled=true";
    }

    /**
     * Cancel an Appointment (Patient Side)
     */
    @PostMapping("/patient/appointment/cancel")
    public String cancelAppointment(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Optional<Payment> paymentOpt = paymentRepository.findByAppointmentId(id);

        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            String paymentStatus = payment.getStatus();

            // STRICT HARD STOP: Reject cancellation entirely if a reference string exists
            if (payment.getReferenceNumber() != null && !payment.getReferenceNumber().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cannot cancel an appointment once a payment reference number has been submitted. Please reschedule instead.");
                return "redirect:/patient/appointment";
            }

            if (!"NOT PAID".equalsIgnoreCase(paymentStatus)) {
                String displayStatus = paymentStatus.equalsIgnoreCase("PAID") ? "marked as paid" : paymentStatus.toLowerCase();
                redirectAttributes.addFlashAttribute("error", "Cannot cancel an appointment that is already " + displayStatus + ".");
                return "redirect:/patient/appointment";
            }

            payment.setStatus("CANCELLED");
            paymentRepository.save(payment);
        }

        app.setStatus("CANCELLED");
        appointmentRepository.save(app);

        historyService.createHistoryRecord(app);

        redirectAttributes.addFlashAttribute("success", "Your appointment and associated payment request have been successfully cancelled.");
        return "redirect:/patient/appointment?cancelled=true";
    }

    @PostMapping("/doctor/appointment/update-status")
    public String updateAppointmentStatus(
            @RequestParam Long appointmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            RedirectAttributes redirectAttributes) {

        Appointment app = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        LocalDateTime appTime = LocalDateTime.of(app.getAppointmentDate(), app.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        if (!appTime.isAfter(now)) {
            redirectAttributes.addFlashAttribute("error", "This appointment has already started or passed. It is now read-only.");
            return "redirect:/doctor/appointment?date=" + app.getAppointmentDate();
        }

        if ("COMPLETED".equalsIgnoreCase(status)) {
            redirectAttributes.addFlashAttribute("error", "Cannot mark future appointments as Completed. Please wait until the appointment time.");
            return "redirect:/doctor/appointment?date=" + app.getAppointmentDate();
        }

        if (status != null && !status.isEmpty()) {
            app.setStatus(status);
        }
        app.setLocation(location);
        appointmentRepository.save(app);
        historyService.createHistoryRecord(app);

        redirectAttributes.addFlashAttribute("success", "Appointment updated successfully.");
        return "redirect:/doctor/appointment?date=" + app.getAppointmentDate();
    }
}