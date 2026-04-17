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

    // Create Appointment
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

        List<String> errors = appointmentService.createAppointment(doctorId, reason, appointmentDate, startTime, patient);

        // Call service to save
        appointmentService.createAppointment(doctorId, reason, appointmentDate, startTime, patient);

        if (!errors.isEmpty()) {
            // Flash attributes survive the redirect once
            redirectAttributes.addFlashAttribute("errorList", errors);
            return "redirect:/patient/appointment";
        }

        // Redirect back to the View Controller mapping
        return "redirect:/patient/appointment?success=true";
    }

    @PostMapping("/patient/appointment/cancel")
    public String cancelAppointment(@RequestParam Long id) {
        appointmentRepository.deleteById(id);
        return "redirect:/patient/appointment?cancelled=true";
    }
}
