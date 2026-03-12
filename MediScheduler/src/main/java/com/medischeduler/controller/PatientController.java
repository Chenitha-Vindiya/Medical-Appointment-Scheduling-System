package com.medischeduler.controller;

import com.medischeduler.model.Patient;
import com.medischeduler.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam String email, Model model) {
        Patient patient = patientService.getPatientByEmail(email);
        model.addAttribute("patient", patient);
        return "dashboard"; // maps to dashboard.jsp
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Patient patient) {
        patientService.registerPatient(patient);
        return "redirect:/login"; // maps to login.jsp
    }
}