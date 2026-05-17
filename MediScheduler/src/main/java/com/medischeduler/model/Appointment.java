package com.medischeduler.model;

import com.medischeduler.model.Doctor;
import com.medischeduler.model.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(nullable = false)
    private String paymentMethod;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private List<History> histories;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public History getHistory() {
        if (this.histories == null || this.histories.isEmpty()) {
            return null;
        }
        // Always return the COMPLETED record (where notes live), or the most recent one
        return this.histories.stream()
                .filter(h -> "COMPLETED".equalsIgnoreCase(h.getStatus()))
                .findFirst()
                .orElse(this.histories.get(this.histories.size() - 1));
    }
}