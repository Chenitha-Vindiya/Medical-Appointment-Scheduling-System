package com.medischeduler.controller;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.WorkingHours;
import com.medischeduler.repository.AppointmentRepository;
import com.medischeduler.repository.DoctorRepository;
import com.medischeduler.repository.PatientRepository;
import com.medischeduler.service.FeedbackService;
import com.medischeduler.service.HistoryService;
import com.medischeduler.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import com.medischeduler.model.Patient;
import com.medischeduler.model.Doctor;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/index")
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
            return "redirect:/login";
        }

        // 1. Get Current Point in Time
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 2. Fetch all potential appointments from today onwards
        List<Appointment> allUpcoming = appointmentRepository
                .findByPatientIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(patient.getId(), today);

        // 3. Filter precisely by Time
        // This ensures we exclude appointments that were at 10:00 AM if it's currently 2:00 PM
        List<Appointment> filteredUpcoming = allUpcoming.stream()
                .filter(app -> {
                    LocalDateTime appDateTime = LocalDateTime.of(app.getAppointmentDate(), app.getStartTime());
                    return !appDateTime.isBefore(now);
                })
                .collect(Collectors.toList());

        // 4. Set "Next Appointment" Card (The single soonest one)
        if (!filteredUpcoming.isEmpty()) {
            model.addAttribute("nextAppointment", filteredUpcoming.get(0));
        }

        // 5. Set "Upcoming Appointments" Table (Top 5 items)
        List<Appointment> tableData = filteredUpcoming.stream()
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("upcomingAppointments", tableData);
        model.addAttribute("patientName", patient.getFirstName());

        return "patient/dashboard";
    }

    @GetMapping("/patient/appointment")
    public String appointment(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) return "redirect:/login";

        // 1. Fetch active doctors for the "New Appointment" modal
        model.addAttribute("doctors", doctorRepository.findByActiveTrue());

        // 2. Fetch future appointments ONLY
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> futureAppointments = appointmentRepository.findByPatientId(patient.getId())
                .stream()
                .filter(app -> {
                    LocalDateTime appTime = LocalDateTime.of(app.getAppointmentDate(), app.getStartTime());
                    return !appTime.isBefore(now);
                })
                .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                .collect(Collectors.toList());

        model.addAttribute("appointments", futureAppointments);

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
    public String showHistory(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) return "redirect:/login";

        model.addAttribute("historyList", historyService.getPatientHistory(patient.getId()));
        return "patient/history";
    }

    @GetMapping("/patient/feedback")
    public String feedback(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) return "redirect:/login";

        // 1. Load appointments for the "New Feedback" modal dropdown
        model.addAttribute("appointments", feedbackService.getEligibleAppointments(patient.getId()));

        // 2. Load existing feedback to display in the list
        model.addAttribute("feedbackList", feedbackService.getFeedbackByPatient(patient.getId()));

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
        Doctor sessionDoc = (Doctor) session.getAttribute("loggedInDoctor");
        if (sessionDoc == null) return "redirect:/login";

        Doctor doctor = doctorRepository.findById(sessionDoc.getId()).orElse(null);
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 1. Fetch today's appointments
        List<Appointment> todayAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(doctor.getId(), today);

        // 2. Find Next Appointment
        LocalTime nextTime = todayAppointments.stream()
                .map(Appointment::getStartTime)
                .filter(t -> t.isAfter(now))
                .findFirst().orElse(null);

        // 3. Consultation Hours Logic
        double totalHours = 0;
        String dayOfWeek = today.getDayOfWeek().name();

        // Check if doctor has a shift today
        WorkingHours todayShift = doctor.getWorkingHours().stream()
                .filter(wh -> wh.getDay().equalsIgnoreCase(dayOfWeek) && wh.isActive())
                .findFirst().orElse(null);

        if (todayShift != null) {
            long minutes = java.time.Duration.between(todayShift.getStartTime(), todayShift.getEndTime()).toMinutes();
            totalHours = minutes / 60.0;
        }

        // 2. Filter today's appointments for ONLY those starting from now onwards
        List<Appointment> futureAppointments = todayAppointments.stream()
                .filter(app -> !app.getStartTime().isBefore(now)) // same as isAfter or isEqual
                .collect(Collectors.toList());

        model.addAttribute("upcomingTable", futureAppointments);
        model.addAttribute("todayCount", todayAppointments.size());
        model.addAttribute("nextTime", nextTime);
        model.addAttribute("consultationHours", totalHours);
        model.addAttribute("isWorkingToday", todayShift != null); // New flag for the UI

        return "doctor/dashboard";
    }

    //Doctor's Appointment
    @GetMapping("/doctor/appointment")
    public String doctorAppointments(
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
            HttpSession session,
            Model model) {

        Doctor sessionDoc = (Doctor) session.getAttribute("loggedInDoctor");
        if (sessionDoc == null) return "redirect:/login";

        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        // 1. Re-fetch doctor
        Doctor doctor = doctorRepository.findById(sessionDoc.getId()).orElse(null);
        String dayOfWeek = selectedDate.getDayOfWeek().name();

        // 2. Get today's working hours (keep this to show "No working hours" msg if needed)
        WorkingHours selectedDayHours = doctor.getWorkingHours().stream()
                .filter(wh -> wh.getDay().equalsIgnoreCase(dayOfWeek) && wh.isActive())
                .findFirst().orElse(null);

        // 3. Get actual bookings for the SELECTED date
        List<Appointment> bookedList = appointmentRepository
                .findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(doctor.getId(), selectedDate);

        // 4. Add navigation and data to model
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("prevDate", selectedDate.minusDays(1));
        model.addAttribute("nextDate", selectedDate.plusDays(1));
        model.addAttribute("isToday", selectedDate.equals(LocalDate.now()));

        // This is the important part for only showing existing appointments
        model.addAttribute("bookedAppointments", bookedList);
        model.addAttribute("todayHours", selectedDayHours);

        return "doctor/appointment";
    }

    //Doctor's Patient
    @GetMapping("/doctor/patient")
    public String doctorPatient(HttpSession session, Model model) {
        Doctor sessionDoc = (Doctor) session.getAttribute("loggedInDoctor");
        if (sessionDoc == null) {
            return "redirect:/login";
        }

        // Call the service to handle the complex business logic
        Map<String, Object> patientDetails = patientService.getDoctorPatientDetails(sessionDoc.getId());

        // Unpack the map and add to the model for Thymeleaf
        model.addAttribute("patients", patientDetails.get("patients"));
        model.addAttribute("lastVisits", patientDetails.get("lastVisits"));
        model.addAttribute("nextAppts", patientDetails.get("nextAppts"));
        model.addAttribute("conditions", patientDetails.get("conditions"));

        return "doctor/patient";
    }

    @GetMapping("/doctor/history")
    public String doctorHistory(HttpSession session, Model model) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (doctor == null) return "redirect:/doctor/login";

        model.addAttribute("historyList", historyService.getDoctorHistory(doctor.getId()));
        return "doctor/history";
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