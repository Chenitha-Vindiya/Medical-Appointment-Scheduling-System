package com.medischeduler.controller;

import com.medischeduler.model.Doctor;
import com.medischeduler.model.Patient;
import com.medischeduler.service.HistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patient/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @PostMapping("/delete")
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

    @PostMapping("/note/update")
    public String updateNote(@RequestParam Long historyId, @RequestParam String extraNote,
                             HttpSession session, RedirectAttributes ra) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (doctor == null) return "redirect:/doctor/login";

        try {
            historyService.addDoctorNote(historyId, extraNote, doctor.getId());
            ra.addFlashAttribute("success", "Clinical note updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/doctor/history";
    }
}