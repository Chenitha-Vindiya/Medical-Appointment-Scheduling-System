package com.medischeduler.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import com.medischeduler.model.Patient;
import com.medischeduler.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Handles the "Create My Account" form from login.html
     */
    @PostMapping("/register")
    public String registerPatient(@ModelAttribute Patient patient, Model model) {
        try {
            // Check if email already exists to prevent duplicates
            if (patientRepository.findByEmail(patient.getEmail()) != null) {
                model.addAttribute("registerError", "Email is already in use.");
                return "login"; // Returns to login page to show error
            }
            // Save the new patient to medischeduler_db in XAMPP
            patientRepository.save(patient);
            return "redirect:/login?success=true"; // Redirect to login with a success message in the URL
        } catch (Exception e) {
            model.addAttribute("registerError", "An error occurred during registration.");
            return "login";
        }
    }

    @PostMapping("/login")
    public String loginPatient(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        // 1. Find the patient by email (the 'username' field in your form)
        Patient patient = patientRepository.findByEmail(username);

        // 2. Check if patient exists and password matches
        if (patient != null && patient.getPassword().equals(password)) {
            // 3. Store the patient object in the session to keep them logged in
            session.setAttribute("loggedInPatient", patient);
            return "redirect:/patient/dashboard";
        } else {
            // 4. If login fails, send an error message back to the login page
            model.addAttribute("signinError", "Invalid email or password.");
            return "login";
        }
    }

    /**
     * Handles "Save Changes" from profile.html
     */
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Patient patient) {
        // JPA .save() will update the existing record based on the ID
        patientRepository.save(patient);
        return "redirect:/patient/profile?updated=true";
    }
}