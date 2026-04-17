package com.medischeduler.model;

import com.medischeduler.model.Doctor;
import com.medischeduler.model.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data // Generates Getters, Setters, ToString (if using Lombok)
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fixed duration constant (30 minutes)
    @Transient // This prevents JPA from creating a column in the DB
    public static final int DURATION_MINUTES = 30;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private java.time.LocalDate appointmentDate;

    @Column(nullable = false)
    private java.time.LocalTime startTime;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(length = 255)
    private String location;

}