package com.medischeduler.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "reference_number")
    private String referenceNumber;

    private String paymentMethod; // "ONLINE", "CASH", "CARD"
    private Double amount; // Will be null initially for manual entry
    private String status; // "PENDING", "COMPLETED", "NOT PAID", "PROCESSING", "PAID", "CANCELLED"

    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Identifies initial ONLINE bookings where the user hasn't paid yet,
     * but the appointment is still PENDING doctor approval.
     */
    public boolean isAwaitingDoctorConfirmation() {
        if (!"ONLINE".equalsIgnoreCase(this.paymentMethod) || this.appointment == null) {
            return false;
        }
        return "PENDING".equalsIgnoreCase(this.appointment.getStatus()) && "NOT PAID".equalsIgnoreCase(this.status);
    }

    /**
     * Determines if the standard payment action forms or verification boxes should render.
     */
    public boolean isActionRequired() {
        // 1. Must be an online payment method
        if (!"ONLINE".equalsIgnoreCase(this.paymentMethod) || this.appointment == null) {
            return false;
        }

        String appStatus = this.appointment.getStatus();

        // 2. Hide everything if the appointment was cancelled
        if ("CANCELLED".equalsIgnoreCase(appStatus)) {
            return false;
        }

        // 3. OVERRIDE: If appointment is PENDING but payment is already PROCESSING,
        // keep showing the verification box. This protects patients who rescheduled.
        if ("PENDING".equalsIgnoreCase(appStatus) && "PROCESSING".equalsIgnoreCase(this.status)) {
            return true;
        }

        // 4. Otherwise, hide the transfer actions while the appointment is unconfirmed
        if ("PENDING".equalsIgnoreCase(appStatus)) {
            return false;
        }

        // 5. Only show if it's not paid yet OR if it's currently being verified
        return "NOT PAID".equalsIgnoreCase(this.status) || "PROCESSING".equalsIgnoreCase(this.status);
    }
}