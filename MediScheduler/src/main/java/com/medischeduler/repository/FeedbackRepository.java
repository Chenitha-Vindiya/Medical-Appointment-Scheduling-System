package com.medischeduler.repository;

import com.medischeduler.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    boolean existsByAppointmentId(Long appointmentId);

    List<Feedback> findByAppointmentDoctorIdOrderByCreatedAtDesc(Long id);
}