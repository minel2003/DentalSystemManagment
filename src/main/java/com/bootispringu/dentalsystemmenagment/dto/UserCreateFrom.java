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
public class UserCreateFrom {

        @NotBlank(message = "Username is required!")
        private String username;

        @NotBlank(message = "Password is required!")
        private String password;

        @NotNull(message = "Role is required!")
        private String role;
    }

