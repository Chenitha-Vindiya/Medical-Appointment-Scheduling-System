package com.medischeduler.controller;

import com.medischeduler.model.*;
import com.medischeduler.repository.FeedbackRepository;
import com.medischeduler.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patient/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback,
                                 @RequestParam Long appointmentId,
                                 @RequestParam(required = false) String[] feedbackTypes,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) return "redirect:/login";

        boolean alreadyExists = feedbackRepository.existsByAppointmentId(appointmentId);
        if (alreadyExists) {
            redirectAttributes.addFlashAttribute("error", "Feedback has already been submitted for this appointment.");
            return "redirect:/patient/feedback";
        }

        if (feedbackTypes != null) {
            feedback.setFeedbackType(String.join(", ", feedbackTypes));
        }

        // Safety bound fallback to catch missing payloads
        if (feedback.getRating() == null) {
            feedback.setRating(5);
        }

        feedbackService.saveFeedback(feedback, patient.getId(), appointmentId);
        redirectAttributes.addFlashAttribute("success", "Thank you for sharing your experience!");
        return "redirect:/patient/feedback";
    }

    @PostMapping("/delete")
    public String deleteFeedback(@RequestParam Long feedbackId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) return "redirect:/login";

        // Call service to delete
        feedbackService.deleteFeedback(feedbackId, patient.getId());

        redirectAttributes.addFlashAttribute("success", "Feedback deleted successfully.");
        return "redirect:/patient/feedback";
    }

    @PostMapping("/update")
    public String updateFeedback(@RequestParam Long id,
                                 @RequestParam(required = false) String[] feedbackTypes,
                                 @RequestParam String content,
                                 @RequestParam Integer rating,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) return "redirect:/login";

        try {
            feedbackService.updateFeedback(id, feedbackTypes, content, rating, patient.getId());
            ra.addFlashAttribute("success", "Feedback updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to finalize modifications: " + e.getMessage());
        }

        return "redirect:/patient/feedback";
    }
}