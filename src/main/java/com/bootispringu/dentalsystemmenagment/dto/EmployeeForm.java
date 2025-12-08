package com.bootispringu.dentalsystemmenagment.dto;

import com.bootispringu.dentalsystemmenagment.Entity.Role;
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
public class EmployeeForm {

    private Long id; // optional for updates

    @NotBlank(message = "First name is required!")
    private String firstName;

    @NotBlank(message = "Father name is required!")
    private String fatherName;

    @NotBlank(message = "Last name is required!")
    private String lastName;

    @NotBlank(message = "Phone number is required!")
    private String phoneNumber;

    @NotBlank(message = "Email is required!")
    @Email(message = "Please provide a valid email address!")
    private String email;

    @NotBlank(message = "Address is required!")
    private String address;

    @NotBlank(message = "City is required!")
    private String city;

    @NotBlank(message = "State is required!")
    private String state;

    @NotNull(message = "Birth date is required!")
    private LocalDate birthDate;

    @NotBlank(message = "Gender is required!")
    private String gender;

    private String position; // optional

    @NotNull(message = "Role is required!")
    private Role role; // DOCTOR, RECEPTIONIST, etc.

    private String specialization; // optional

    private Long departmentId; // link by id, optional

    @NotNull(message = "Hire date is required!")
    private LocalDate hireDate;

    private Double salary; // optional

    @NotBlank(message = "Status is required!")
    private String status; // Active, Inactive
}