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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            e.printStackTrace(); // This will print the exact error (e.g., Null constraint violation) to your IntelliJ console
            model.addAttribute("registerError", "An error occurred: " + e.getMessage());
            return "login";
        }
    }

    @PostMapping("/login")
    public String loginPatient(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        // 1. Find the patient by email only (don't check 'active' yet)
        Patient patient = patientRepository.findByEmail(username);

        // 2. Verify the patient exists and the password matches the hash in XAMPP
        if (patient != null && passwordEncoder.matches(password, patient.getPassword())) {

            // 3. Check if the account is active
            if (patient.isActive()) {
                session.setAttribute("loggedInPatient", patient);
                return "redirect:/patient/dashboard";
            } else {
                // Correct password, but the account is deactivated
                return "redirect:/patient/deactivated";
            }
        } else {
            // Either the email doesn't exist or the password is wrong
            model.addAttribute("signinError", "Invalid email or password.");
            return "login";
        }
    }

    @PostMapping("/deactivate-account")
    public String deactivateAccount(HttpSession session, RedirectAttributes redirAttrs) {
        // 1. Get the current patient from the session
        Patient sessionPatient = (Patient) session.getAttribute("loggedInPatient");

        if (sessionPatient != null) {
            // 2. Fetch fresh data from DB to ensure we have the correct ID
            Patient existingPatient = patientRepository.findById(sessionPatient.getId()).orElse(null);

            if (existingPatient != null) {
                // 3. Set status to false (Soft Delete)
                existingPatient.setActive(false);

                // 4. Save the change back to XAMPP/MySQL
                patientRepository.save(existingPatient);

                // 5. Invalidate the session to log them out immediately
                session.invalidate();

                // 6. Add a flash attribute for the login page notification
                redirAttrs.addFlashAttribute("deactivatedMessage", "Your account has been deactivated.");

                return "redirect:/deactivated";
            }
        }

        return "redirect:/login";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Patient formPatient, HttpSession session) {
        // 1. Fetch the existing patient from DB to ensure we don't lose the hashed password
        Patient existingPatient = patientRepository.findById(formPatient.getId()).orElse(null);

        if (existingPatient != null) {
            // 2. Map only the profile and emergency fields
            existingPatient.setFirstName(formPatient.getFirstName());
            existingPatient.setLastName(formPatient.getLastName());
            existingPatient.setDateOfBirth(formPatient.getDateOfBirth());
            existingPatient.setGender(formPatient.getGender());
            existingPatient.setNationalId(formPatient.getNationalId());
            existingPatient.setEmail(formPatient.getEmail());
            existingPatient.setPhoneNumber(formPatient.getPhoneNumber());
            existingPatient.setHomeAddress(formPatient.getHomeAddress());

            // Update the new fields you added
            existingPatient.setEmergencyContactName(formPatient.getEmergencyContactName());
            existingPatient.setRelationship(formPatient.getRelationship());
            existingPatient.setEmergencyPhone(formPatient.getEmergencyPhone());

            // Note: Password logic is removed from here to prevent accidental overwrites

            // 3. Save the updated object
            patientRepository.save(existingPatient);

            // 4. Update the session so the UI (like headers) refreshes immediately
            session.setAttribute("loggedInPatient", existingPatient);
        }

        return "redirect:/patient/profile?updated=true";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirAttrs) {

        Patient sessionPatient = (Patient) session.getAttribute("loggedInPatient");

        // Safety check if session expired
        if (sessionPatient == null) {
            return "redirect:/login";
        }

        // 1. Check if New and Confirm are equal
        if (!newPassword.equals(confirmPassword)) {
            redirAttrs.addFlashAttribute("passwordError", "New passwords do not match.");
            return "redirect:/patient/profile";
        }

        // 2. Enforce Password Rules (8 chars)
        if (newPassword.length() < 8) {
            redirAttrs.addFlashAttribute("passwordError", "New password must be at least 8 characters long.");
            return "redirect:/patient/profile";
        }

        // 3. Verify Existing Password
        // We fetch a fresh instance from the DB using the ID to avoid detached entity issues
        Patient existingPatient = patientRepository.findById(sessionPatient.getId()).orElse(null);

        if (existingPatient != null && passwordEncoder.matches(currentPassword, existingPatient.getPassword())) {

            // 4. Update and Hash the new password
            existingPatient.setPassword(passwordEncoder.encode(newPassword));
            patientRepository.save(existingPatient);

            // 5. Update the session with the updated object
            session.setAttribute("loggedInPatient", existingPatient);

            redirAttrs.addFlashAttribute("passwordSuccess", "Password updated successfully!");
        } else {
            // Current password didn't match the one in XAMPP/MySQL
            redirAttrs.addFlashAttribute("passwordError", "The current password you entered is incorrect.");
        }

        return "redirect:/patient/profile";
    }
}