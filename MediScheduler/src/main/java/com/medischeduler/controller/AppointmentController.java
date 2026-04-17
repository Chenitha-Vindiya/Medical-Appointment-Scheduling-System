package com.medischeduler.controller;

import com.medischeduler.model.Patient;
import com.medischeduler.repository.AppointmentRepository;
import com.medischeduler.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Create a new Appointment
     */
    @PostMapping("/patient/appointment/create")
    public String createAppointment(
            @RequestParam Long doctorId,
            @RequestParam String reason,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient == null) {
            return "redirect:/login";
        }

        // Fixed: We only call the service ONCE.
        // The service internally handles the save if there are no errors.
        List<String> errors = appointmentService.createAppointment(
                doctorId, reason, appointmentDate, startTime, patient);

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorList", errors);
            return "redirect:/patient/appointment";
        }

        return "redirect:/patient/appointment?success=true";
    }

    /**
     * Reschedule an existing Appointment
     * Note: We don't take a doctorId here because rescheduling
     * should not allow changing the doctor.
     */
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

        // Call service to validate and update the existing record
        List<String> errors = appointmentService.rescheduleAppointment(
                appointmentId, appointmentDate, startTime, reason);

        if (!errors.isEmpty()) {
            // 1. Pass the errors
            redirectAttributes.addFlashAttribute("errorList", errors);

            // 2. Pass the specific flag for Reschedule
            redirectAttributes.addFlashAttribute("isRescheduleError", true);

            // 3. Keep your URL parameter for extra safety
            return "redirect:/patient/appointment?editError=true";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully!");
        return "redirect:/patient/appointment?rescheduled=true";
    }

    /**
     * Cancel/Delete an Appointment
     */
    @PostMapping("/patient/appointment/cancel")
    public String cancelAppointment(@RequestParam Long id) {
        appointmentRepository.deleteById(id);
        return "redirect:/patient/appointment?cancelled=true";
    }
}