package com.medischeduler.controller;

import com.medischeduler.model.*;
import com.medischeduler.repository.FeedbackRepository;
import com.medischeduler.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/patient/feedback/submit")
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

    @PostMapping("/patient/feedback/delete")
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

    @PostMapping("/patient/feedback/update")
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

    @PostMapping("/doctor/feedback/respond")
    @ResponseBody
    public ResponseEntity<?> respondToFeedback(@RequestParam Long feedbackId,
                                               @RequestParam String responseContent,
                                               HttpSession session) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (doctor == null) return ResponseEntity.status(401).build();

        feedbackService.respondToFeedback(feedbackId, responseContent, doctor.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/doctor/feedback/acknowledge")
    @ResponseBody
    public ResponseEntity<?> acknowledgeFeedback(@RequestParam Long feedbackId,
                                                 @RequestParam boolean status,
                                                 HttpSession session) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (doctor == null) return ResponseEntity.status(401).build();

        feedbackService.toggleAcknowledgment(feedbackId, status, doctor.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/doctor/feedback/escalate")
    @ResponseBody
    public ResponseEntity<?> escalateFeedback(@RequestParam Long feedbackId,
                                              @RequestParam boolean status, // NEW
                                              HttpSession session) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (doctor == null) return ResponseEntity.status(401).build();

        feedbackService.escalateFeedback(feedbackId, status, doctor.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/doctor/feedback/delete-reply")
    @ResponseBody
    public ResponseEntity<?> deleteReply(@RequestParam Long feedbackId, HttpSession session) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (doctor == null) return ResponseEntity.status(401).build();

        Feedback feedback = feedbackRepository.findById(feedbackId).orElse(null);
        if (feedback != null && feedback.getAppointment().getDoctor().getId().equals(doctor.getId())) {
            feedback.setResponseContent(null);
            feedback.setRespondedAt(null);
            feedbackRepository.save(feedback);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(400).build();
    }
}