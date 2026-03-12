package com.medischeduler.controller;

import com.medischeduler.model.Patient;
import com.medischeduler.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * FIX: Dashboard now reads the logged-in patient from the session,
     * not from an ?email= URL parameter (which allowed any user to view
     * any patient's data by simply changing the parameter).
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) {
            // Not logged in — redirect to login page
            return "redirect:/login";
        }
        model.addAttribute("patient", patient);
        return "dashboard";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Patient patient) {
        patientService.registerPatient(patient);
        return "redirect:/login";
    }

    /**
     * FIX: Added login POST handler that was previously missing entirely.
     * Validates credentials and stores the authenticated patient in the session.
     */
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Patient patient = patientService.authenticate(email, password);
        if (patient == null) {
            model.addAttribute("loginError", "Invalid email or password.");
            return "login";
        }
        session.setAttribute("loggedInPatient", patient);
        return "redirect:/patient/dashboard";
    }

    /**
     * Logout: invalidates the session and redirects to the login page.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}