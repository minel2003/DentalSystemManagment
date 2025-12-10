package com.bootispringu.dentalsystemmenagment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentTypeForm {

    private Long departmentTypeId;

    @NotBlank(message = "Department type name is required!")
    private String name;

    private String description;

    @NotBlank(message = "Status is required!")
    private String status;
}

