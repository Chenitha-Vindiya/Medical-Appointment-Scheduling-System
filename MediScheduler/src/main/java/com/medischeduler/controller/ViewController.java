package com.medischeduler.controller;

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

    @GetMapping("/dashboard")
    public String dashboard() {
        // Points to src/main/resources/templates/patient/dashboard.html
        return "patient/dashboard";
    }

    @GetMapping("/appointment")
    public String appointment() {
        return "patient/appointment";
    }

    @GetMapping("/billing")
    public String billing() {
        return "patient/billing";
    }

    @GetMapping("/history")
    public String history() {
        return "patient/history";
    }

    @GetMapping("/feedback")
    public String feedback() {
        // Looks for src/main/resources/templates/patient/feedback.html
        return "patient/feedback";
    }

    @GetMapping("/profile")
    public String profile() {
        return "patient/profile";
    }
}