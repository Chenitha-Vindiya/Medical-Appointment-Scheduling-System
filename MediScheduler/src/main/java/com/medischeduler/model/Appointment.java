package com.medischeduler.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity

@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String doctorName;
    private String specialty;
    private LocalDateTime appointmentTime;
    private String status; // e.g., Confirmed, Pending, Cancelled
    private String type;   // e.g., Virtual, In-person

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
}