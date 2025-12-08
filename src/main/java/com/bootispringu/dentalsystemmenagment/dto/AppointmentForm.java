package com.bootispringu.dentalsystemmenagment.dto;

import com.bootispringu.dentalsystemmenagment.Entity.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppointmentForm {

    private Long id; // optional for updates

    @NotNull(message = "Date is required!")
    private LocalDate date; // separate date

    @NotNull(message = "Time is required!")
    private LocalTime time;
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @NotNull(message = "Status is required!")
    private Status status; // SCHEDULED, COMPLETED, CANCELLED

    @NotNull(message = "Patient is required!")
    private Long patientId; // selected patient id from dropdown

    @NotNull(message = "Doctor is required!")
    private Long doctorId; // selected doctor id from dropdown
}
