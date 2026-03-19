package com.medischeduler.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Data // Use Lombok to generate Getters/Setters automatically
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    // Personal Info
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;

    @Column(unique = true)
    private String nationalId;

    // Contact Details
    @Column(unique = true)
    private String email;
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String homeAddress;

    // Emergency Contact
    private String emergencyContactName;
    private String relationship;
    private String emergencyPhone;

    // Security (Typically mapped to a User entity in larger apps)
    private String password;

}