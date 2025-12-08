package com.bootispringu.dentalsystemmenagment.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "d_patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "patient_personal_number")
    private String patientPersonalNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "last_name")
    private String lastName;

    // Contact Information
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "email",unique = true)
    private String email;
    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    // Personal Information
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String gender;

    // Medical Information
    @Column(name = "medical_history")
    private String medicalHistory;

    @Column(name = "alergies")
    private String alergies;

    @Column(name = "current_medication")
    private String currentMedications;

    @Column(name = "blood_type")
    private String bloodType;

    // System Information
    @Column(name = "status")
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;





    //Relations
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointments;
}
