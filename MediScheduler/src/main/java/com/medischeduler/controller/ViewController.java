package com.medischeduler.controller;


import org.springframework.ui.Model;
import com.medischeduler.model.Patient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index"; // This points to src/main/resources/templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // This points to src/main/resources/templates/login.html
    }

    @GetMapping("/patient/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        model.addAttribute("patientName", patient.getFirstName());
        return "patient/dashboard";
    }

    @GetMapping("/patient/appointment")
    public String appointment() {
        return "patient/appointment";
    }

    @GetMapping("/patient/billing")
    public String billing() {
        return "patient/billing";
    }

    @GetMapping("/patient/history")
    public String history() {
        return "patient/history";
    }

    @GetMapping("/patient/feedback")
    public String feedback() {
        // Looks for src/main/resources/templates/patient/feedback.html
        return "patient/feedback";
    }

    @GetMapping("/patient/profile")
    public String showProfile(HttpSession session, Model model) {
        // 1. Get the patient from the session
        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        // 2. Safety Check: If session is null, redirect to login instead of crashing
        if (patient == null) {
            return "redirect:/login";
        }

        // 3. Pass the patient object to the HTML
        model.addAttribute("patient", patient);
        return "patient/profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clears all session data
        return "redirect:/login?logout=true";
    }
}