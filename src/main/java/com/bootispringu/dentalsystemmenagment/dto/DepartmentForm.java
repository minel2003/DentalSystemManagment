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
public class DepartmentForm {

    private Long departmentId;

    @NotBlank(message = "Department name is required!")
    private String name;

    private String description;

    private String location;

    @NotBlank(message = "Department type is required!")
    private String departmentTypeName;

    private String category;

    @NotBlank(message = "Status is required!")
    private String status;
}

