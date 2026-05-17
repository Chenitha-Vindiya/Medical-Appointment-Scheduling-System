package com.medischeduler.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private String subject;
    private String feedbackType;

    @Column(nullable = false)
    private Integer rating = 0;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String responseContent; // Stores the written reply

    @Column(nullable = false)
    private Boolean acknowledged = false; // Tracks heart clicks

    @Column(nullable = false)
    private Boolean escalated = false; // Tracks concern routing

    @Column(name = "responded_at")
    private LocalDateTime respondedAt; // Timestamp for when the doctor interacted
}