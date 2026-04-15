package com.medischeduler.controller;

import com.medischeduler.model.Patient;
import com.medischeduler.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/register")
    public String registerPatient(@ModelAttribute Patient patient, Model model) {
        try {
            patientService.registerPatient(patient);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            // Split the message back into individual errors
            String[] errorArray = e.getMessage().split("\\|");
            model.addAttribute("registerErrors", errorArray);
            model.addAttribute("activeTab", "register");
            return "login";
        }
    }

    @PostMapping("/login")
    public String loginPatient(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Patient patient = patientService.authenticate(username, password);

        if (patient != null) {
            if (patient.isActive()) {
                session.setAttribute("loggedInPatient", patient);
                return "redirect:/patient/dashboard";
            } else {
                model.addAttribute("signinErrors", List.of("This account has been deactivated."));
                model.addAttribute("activeTab", "signin");
                return "login";
            }
        } else {
            // Use "signinErrors" to match the multiple-error loop logic
            model.addAttribute("signinErrors", List.of("Invalid email or password."));
            model.addAttribute("activeTab", "signin");
            return "login";
        }
    }

    @PostMapping("/deactivate-account")
    public String deactivateAccount(HttpSession session, RedirectAttributes redirAttrs) {
        Patient sessionPatient = (Patient) session.getAttribute("loggedInPatient");

        if (sessionPatient != null && patientService.deactivateAccount(sessionPatient.getId())) {
            session.invalidate();
            redirAttrs.addFlashAttribute("deactivatedMessage", "Your account has been deactivated.");
            return "redirect:/patient/deactivated";
        }
        return "redirect:/login";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Patient formPatient, HttpSession session) {
        Patient updated = patientService.updateProfile(formPatient);
        if (updated != null) {
            session.setAttribute("loggedInPatient", updated);
        }
        return "redirect:/patient/profile?updated=true";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword, @RequestParam String newPassword,
                                 @RequestParam String confirmPassword, HttpSession session, RedirectAttributes redirAttrs) {

        Patient sessionPatient = (Patient) session.getAttribute("loggedInPatient");
        if (sessionPatient == null) return "redirect:/login";

        // Keep your original validation logic in the controller for UI feedback
        if (!newPassword.equals(confirmPassword)) {
            redirAttrs.addFlashAttribute("passwordError", "New passwords do not match.");
            return "redirect:/patient/profile";
        }

        if (newPassword.length() < 8) {
            redirAttrs.addFlashAttribute("passwordError", "New password must be at least 8 characters long.");
            return "redirect:/patient/profile";
        }

        String result = patientService.changePassword(sessionPatient.getId(), currentPassword, newPassword);

        if (result.equals("SUCCESS")) {
            // Re-fetch to update session
            session.setAttribute("loggedInPatient", (Patient) session.getAttribute("loggedInPatient"));
            redirAttrs.addFlashAttribute("passwordSuccess", "Password updated successfully!");
        } else {
            redirAttrs.addFlashAttribute("passwordError", "The current password you entered is incorrect.");
        }

        return "redirect:/patient/profile";
    }
}