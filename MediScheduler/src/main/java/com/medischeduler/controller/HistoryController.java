package com.medischeduler.controller;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.Doctor;
import com.medischeduler.model.History;
import com.medischeduler.model.Patient;
import com.medischeduler.repository.AppointmentRepository;
import com.medischeduler.repository.HistoryRepository;
import com.medischeduler.service.HistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @PostMapping("/patient/history/delete")
    public String deleteHistory(@RequestParam Long historyId, HttpSession session, RedirectAttributes ra) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) return "redirect:/login";

        try {
            historyService.deleteHistoryByPatient(historyId, patient.getId());
            ra.addFlashAttribute("success", "History record removed.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/history";
    }

    @PostMapping("/doctor/history/note/save")
    @ResponseBody
    public ResponseEntity<?> saveClinicalNote(@RequestParam Long appointmentId, @RequestParam String extraNote) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) return ResponseEntity.badRequest().build();

        History history = historyRepository.findByAppointmentId(appointmentId).orElse(new History());
        if (history.getId() == null) {
            history.setAppointment(appointment);
            history.setPatient(appointment.getPatient());
            history.setStatus("COMPLETED");
        }

        history.setExtraNote(extraNote);
        historyRepository.save(history);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/doctor/history/note/delete")
    @ResponseBody
    public ResponseEntity<?> deleteClinicalNote(@RequestParam Long appointmentId) {
        History history = historyRepository.findByAppointmentId(appointmentId).orElse(null);
        if (history != null) {
            history.setExtraNote(null);
            historyRepository.save(history);
        }
        return ResponseEntity.ok().build();
    }
}