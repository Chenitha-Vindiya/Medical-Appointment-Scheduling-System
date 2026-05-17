package com.medischeduler.repository;

import com.medischeduler.model.Payment; // Fixed import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> { // Changed Appointment to Payment

    // Added this so you can easily fetch all payments for the logged-in patient
    List<Payment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    // Optional: Useful if you need to find a specific payment linked to an appointment
    Optional<Payment> findByAppointmentId(Long appointmentId);

    List<Payment> findByAppointmentDoctorIdOrderByCreatedAtDesc(Long doctorId);

    long countByPatientIdAndStatus(Long id, String pending);
}