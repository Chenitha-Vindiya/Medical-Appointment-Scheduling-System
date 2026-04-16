package com.medischeduler.controller;

import com.medischeduler.model.Doctor;
import com.medischeduler.model.Patient;
import com.medischeduler.model.WorkingHours;
import com.medischeduler.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/register")
    public String registerDoctor(@ModelAttribute Doctor doctor, Model model) {
        try {
            doctorService.registerDoctor(doctor);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            String[] errorArray = e.getMessage().split("\\|");
            model.addAttribute("registerErrors", errorArray);
            model.addAttribute("activeTab", "register");
            return "login";
        }
    }

    @PostMapping("/login")
    public String loginDoctor(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Doctor doctor = doctorService.authenticate(username, password);

        if (doctor != null) {
            if (doctor.isActive()) {
                session.setAttribute("loggedInDoctor", doctor);
                return "redirect:/doctor/dashboard";
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
        Doctor sessionDoctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (sessionDoctor != null && doctorService.deactivateAccount(sessionDoctor.getId())) {
            session.invalidate();
            redirAttrs.addFlashAttribute("deactivatedMessage", "Your account has been deactivated.");
            return "redirect:/patient/deactivated";
        }
        return "redirect:/login";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Doctor formDoctor, HttpSession session) {
        // 1. Call the service to update the database
        Doctor updated = doctorService.updateProfile(formDoctor);

        // 2. If the update was successful, refresh the session data
        if (updated != null) {
            session.setAttribute("loggedInDoctor", updated);
        }

        // 3. Redirect back to the profile page with a success parameter
        return "redirect:/doctor/profile?updated=true";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword, @RequestParam String newPassword,
                                 @RequestParam String confirmPassword, HttpSession session, RedirectAttributes redirAttrs) {

        Doctor sessionDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (sessionDoctor == null) return "redirect:/login";

        // Keep your original validation logic in the controller for UI feedback
        if (!newPassword.equals(confirmPassword)) {
            redirAttrs.addFlashAttribute("passwordError", "New passwords do not match.");
            return "redirect:/doctor/profile";
        }

        if (newPassword.length() < 8) {
            redirAttrs.addFlashAttribute("passwordError", "New password must be at least 8 characters long.");
            return "redirect:/doctor/profile";
        }

        String result = doctorService.changePassword(sessionDoctor.getId(), currentPassword, newPassword);

        if (result.equals("SUCCESS")) {
            // Re-fetch to update session
            session.setAttribute("loggedInDoctor", (Doctor) session.getAttribute("loggedInDoctor"));
            redirAttrs.addFlashAttribute("passwordSuccess", "Password updated successfully!");
        } else {
            redirAttrs.addFlashAttribute("passwordError", "The current password you entered is incorrect.");
        }

        return "redirect:/doctor/profile";
    }

    @PostMapping("/update-working-hours")
    public String updateWorkingHours(@ModelAttribute("doctor") Doctor formDoctor, RedirectAttributes redirAttrs) {

        // Pass the ID and the list from the form to the service
        doctorService.updateDoctorWorkingHours(formDoctor.getId(), formDoctor.getWorkingHours());

        // Add success message for the UI
        redirAttrs.addFlashAttribute("updated", true);

        return "redirect:/doctor/profile";
    }
}