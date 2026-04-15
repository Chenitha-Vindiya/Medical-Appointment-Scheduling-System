package com.medischeduler.controller;

import com.medischeduler.model.Doctor;
import com.medischeduler.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            model.addAttribute("registerError", "An error occurred: " + e.getMessage());
            return "login";
        }
    }

    @PostMapping("/login")
    public String loginDoctor(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Doctor doctor = doctorService.authenticate(username, password);

        if (doctor != null && doctor.isActive()) {
            session.setAttribute("loggedInDoctor", doctor);
            return "redirect:/doctor/dashboard";
        } else {
            model.addAttribute("signinError", "Invalid doctor credentials.");
            return "login";
        }
    }
}