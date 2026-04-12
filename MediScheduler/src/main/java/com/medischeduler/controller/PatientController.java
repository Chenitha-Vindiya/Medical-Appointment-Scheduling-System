package com.medischeduler.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registerPatient(@ModelAttribute Patient patient, Model model) {
        try {
            if (patientRepository.findByEmail(patient.getEmail()) != null) {
                model.addAttribute("registerError", "Email is already in use.");
                return "login";
            }

            // Secure the password before saving
            patient.setPassword(passwordEncoder.encode(patient.getPassword()));
            patientRepository.save(patient);

            return "redirect:/login?success=true";
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

        // Use the new repository method to only find active users
        Patient patient = patientRepository.findByEmailAndActiveTrue(username);

        if (patient != null && passwordEncoder.matches(password, patient.getPassword())) {
            session.setAttribute("loggedInPatient", patient);
            return "redirect:/patient/dashboard";
        } else {
            // More specific error message for user experience
            model.addAttribute("signinError", "Invalid credentials or account is deactivated.");
            return "login";
        }
    }

    @PostMapping("/deactivate-account")
    public String deactivateAccount(HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient != null) {
            patient.setActive(false); // Set status to false
            patientRepository.save(patient); // Update in DB
            session.invalidate(); // Log them out
        }

        return "redirect:/login?deactivated=true";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Patient patient, HttpSession session) {
        // Ensure you aren't overwriting the password with a blank value if the user didn't change it
        // Or re-hash if they did. For now, simple save:
        patientRepository.save(patient);
        session.setAttribute("loggedInPatient", patient); // Update session with new data
        return "redirect:/patient/profile?updated=true";
    }
}