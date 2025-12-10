package com.bootispringu.dentalsystemmenagment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptForm {

    private Long receiptId;

    @NotNull(message = "Patient is required!")
    private Long patientId;

    private Long appointmentId;

    @NotNull(message = "Receipt date is required!")
    private LocalDate receiptDate;

    @NotNull(message = "Amount is required!")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required!")
    private String paymentMethod;

    private String description;

    @NotBlank(message = "Status is required!")
    private String status;
}

