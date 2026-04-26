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
                                 RedirectAttributes redirectAttributes) { // Added RedirectAttributes
        Patient patient = (Patient) session.getAttribute("loggedInPatient");
        if (patient == null) return "redirect:/login";

        boolean alreadyExists = feedbackRepository.existsByAppointmentId(appointmentId);
        if (alreadyExists) {
            redirectAttributes.addFlashAttribute("error", "Feedback has already been submitted for this appointment.");
            return "redirect:/patient/feedback";
        }

        // Join array into comma-separated string
        if (feedbackTypes != null) {
            feedback.setFeedbackType(String.join(", ", feedbackTypes));
        }

        feedbackService.saveFeedback(feedback, patient.getId(), appointmentId);
        redirectAttributes.addFlashAttribute("success", "Thank you for your feedback!");
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
}