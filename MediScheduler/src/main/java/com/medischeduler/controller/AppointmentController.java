package com.medischeduler.controller;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.Patient;
import com.medischeduler.repository.AppointmentRepository;
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

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private HistoryService historyService;

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
    /**
     * Cancel an Appointment (Patient Side)
     */
    @PostMapping("/patient/appointment/cancel")
    public String cancelAppointment(@RequestParam Long id) {
        // 1. Fetch the appointment instead of deleting it
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // 2. Change the status
        app.setStatus("CANCELLED");
        appointmentRepository.save(app);

        // 3. Record it in the history timeline!
        historyService.createHistoryRecord(app);

        return "redirect:/patient/appointment?cancelled=true";
    }

    @PostMapping("/doctor/appointment/update-status")
    public String updateAppointmentStatus(
            @RequestParam Long appointmentId,
            @RequestParam(required = false) String status, // Marked required=false to match hidden input
            @RequestParam(required = false) String location,
            RedirectAttributes redirectAttributes) {

        Appointment app = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        LocalDateTime appTime = LocalDateTime.of(app.getAppointmentDate(), app.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        // 1. Block edits if the appointment has already started or passed
        // Using !isBefore(now) handles both 'now' and 'past' appointments
        if (!appTime.isAfter(now)) {
            redirectAttributes.addFlashAttribute("error", "This appointment has already started or passed. It is now read-only.");
            return "redirect:/doctor/appointment?date=" + app.getAppointmentDate();
        }

        // 2. Prevent marking FUTURE appointments as COMPLETED early
        if ("COMPLETED".equalsIgnoreCase(status)) {
            redirectAttributes.addFlashAttribute("error", "Cannot mark future appointments as Completed. Please wait until the appointment time.");
            return "redirect:/doctor/appointment?date=" + app.getAppointmentDate();
        }

        // 3. Success Save (Only update status if it was sent)
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