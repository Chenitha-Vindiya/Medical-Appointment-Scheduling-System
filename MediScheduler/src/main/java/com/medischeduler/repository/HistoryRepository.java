package com.medischeduler.repository;

import com.medischeduler.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    // For Patient: View their own history
    List<History> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    // For Doctor: View history of appointments they conducted
    List<History> findByAppointmentDoctorIdOrderByCreatedAtDesc(Long doctorId);

    Optional<History> findTopByAppointmentIdOrderByCreatedAtDesc(Long appointmentId);

    Optional<History> findByAppointmentId(Long appointmentId);
}