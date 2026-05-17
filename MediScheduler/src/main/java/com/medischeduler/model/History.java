package com.medischeduler.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "histories")
@Data
@NoArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String extraNote;

    // ==========================================
    // NEW: SNAPSHOT FIELDS (Frozen in time)
    // ==========================================
    @Column(name = "historical_date")
    private LocalDate historicalDate;

    @Column(name = "historical_time")
    private LocalTime historicalTime;

    @Column(name = "historical_reason", columnDefinition = "TEXT")
    private String historicalReason;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}