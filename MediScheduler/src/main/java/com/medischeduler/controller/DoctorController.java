package com.medischeduler.controller;

import com.medischeduler.model.Doctor;
import com.medischeduler.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}