package com.bootispringu.dentalsystemmenagment.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientForm {

    private Long patientId; // optional, for updates

    @NotBlank(message = "Personal number is required")
    private String patientPersonalNumber;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Father name is required")
    private String fatherName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    // Contact Information
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String address;

    private String city;

    private String state;

    @NotBlank(message = "Email is required")
    @Email(message = "Provide a valid email")
    private String email;

    // Personal Information
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @NotBlank(message = "Gender is required")
    private String gender;

    // Medical Information
    private String medicalHistory;

    private String alergies;

    private String currentMedications;

    private String bloodType;

    // System Information
    private String status; // Active, Inactive
}