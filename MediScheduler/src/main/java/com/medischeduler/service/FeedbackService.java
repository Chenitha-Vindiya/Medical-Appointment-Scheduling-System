package com.medischeduler.service;

import com.medischeduler.model.*;
import com.medischeduler.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository; // Ensure this is injected

    public List<Appointment> getEligibleAppointments(Long patientId) {
        // 1. Get all appointments for the patient
        List<Appointment> allPatientAppointments = appointmentRepository.findByPatientId(patientId);

        // 2. Get all existing feedback entries for this patient
        List<Feedback> existingFeedbacks = feedbackRepository.findByPatientIdOrderByCreatedAtDesc(patientId);

        // 3. Extract the IDs of appointments that already have feedback
        List<Long> appointmentIdsWithFeedback = existingFeedbacks.stream()
                .map(f -> f.getAppointment().getId())
                .collect(Collectors.toList());

        // 4. Return only appointments that:
        //    a) Are COMPLETED or CANCELLED
        //    b) Do NOT have an ID in the appointmentIdsWithFeedback list
        return allPatientAppointments.stream()
                .filter(a -> (a.getStatus().equalsIgnoreCase("COMPLETED") ||
                        a.getStatus().equalsIgnoreCase("CANCELLED")))
                .filter(a -> !appointmentIdsWithFeedback.contains(a.getId()))
                .collect(Collectors.toList());
    }

    public void saveFeedback(Feedback feedback, Long patientId, Long appointmentId) {
        // Fetch the existing entities to set them in the feedback object
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        feedback.setPatient(patient);
        feedback.setAppointment(appointment);

        feedbackRepository.save(feedback);
    }

    // Add this to fetch history for your feedback list view
    public List<Feedback> getFeedbackByPatient(Long patientId) {
        return feedbackRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public void deleteFeedback(Long feedbackId, Long patientId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        // Security check: ensure the feedback belongs to the logged-in patient
        if (feedback.getPatient().getId().equals(patientId)) {
            feedbackRepository.delete(feedback);
        } else {
            throw new RuntimeException("Unauthorized: You cannot delete this feedback.");
        }
    }

    // Service method extension capturing dynamic ratings updates
    public void updateFeedback(Long id, String[] feedbackTypes, String content, Integer rating, Long patientId) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback instance not found"));

        // Security assertion verification
        if (!feedback.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Unauthorized execution attempt to edit this entry");
        }

        if (feedbackTypes != null) {
            feedback.setFeedbackType(String.join(", ", feedbackTypes));
        }
        feedback.setContent(content);

        // Ensure rating ranges strictly adhere to standard validation constraints
        if (rating != null && rating >= 1 && rating <= 5) {
            feedback.setRating(rating);
        }

        feedbackRepository.save(feedback);
    }
}