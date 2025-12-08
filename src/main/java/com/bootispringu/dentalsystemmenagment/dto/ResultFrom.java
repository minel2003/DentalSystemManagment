package com.bootispringu.dentalsystemmenagment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultFrom {

        @NotNull(message = "Appointment is required")
        private Long appointmentId;

        @NotBlank(message = "Treatment name is required")
        private String treatment;

        @NotBlank(message = "Result notes are required")
        private String notes;

    }


