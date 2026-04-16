package com.medischeduler.controller;

import com.medischeduler.repository.DoctorRepository;
import com.medischeduler.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.medischeduler.model.Patient;
import com.medischeduler.model.Doctor;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @Autowired
    private DoctorRepository doctorRepository;

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
    public String appointment(HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        return "patient/appointment";
    }

    @GetMapping("/patient/payment")
    public String payment(HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        return "patient/payment";
    }

    @GetMapping("/patient/history")
    public String history(HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        return "patient/history";
    }

    @GetMapping("/patient/feedback")
    public String feedback(HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

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

    @GetMapping("/patient/deactivated")
    public String showDeactivatedPage() {
        return "deactivated";
    }

    //Doctor Dashboard
    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInDoctor") == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "doctor/dashboard";
    }

    //Doctor's Appointment
    @GetMapping("/doctor/appointment")
    public String doctorAppointment(HttpSession session, Model model) {
        if (session.getAttribute("loggedInDoctor") == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "doctor/appointment";
    }

    //Doctor's Patient
    @GetMapping("/doctor/patient")
    public String doctorPatient(HttpSession session, Model model) {
        if (session.getAttribute("loggedInDoctor") == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "doctor/patient";
    }

    //Doctor's Payment
    @GetMapping("/doctor/payment")
    public String doctorPayment(HttpSession session, Model model) {
        if (session.getAttribute("loggedInDoctor") == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "doctor/payment";
    }

    //Doctor's Feedback
    @GetMapping("/doctor/feedback")
    public String doctorFeedback(HttpSession session, Model model) {
        if (session.getAttribute("loggedInDoctor") == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "doctor/feedback";
    }

    //Doctor's Profile
    @GetMapping("/doctor/profile")
    public String doctorProfile(HttpSession session, Model model) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (session.getAttribute("loggedInDoctor") == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        doctor = doctorRepository.findById(doctor.getId()).orElse(null);
        model.addAttribute("doctor", doctor);
        return "doctor/profile";
    }
}