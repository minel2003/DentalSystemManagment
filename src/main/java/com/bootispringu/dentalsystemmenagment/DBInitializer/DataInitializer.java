package com.bootispringu.dentalsystemmenagment.DBInitializer;


import com.bootispringu.dentalsystemmenagment.Service.UserAccountService;
import com.bootispringu.dentalsystemmenagment.dto.UserCreateFrom;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserAccountService userAccountService) {
        return args -> {
            // Create Admin if not exists
            try {
                UserCreateFrom adminDto = new UserCreateFrom();
                adminDto.setUsername("a");
                adminDto.setPassword("a");
                adminDto.setRole("ADMIN");
                userAccountService.createUser(adminDto);
                System.out.println("Admin user created!");
            } catch (IllegalArgumentException ignored) {
                System.out.println("Admin already exists");
            }

            try {
                UserCreateFrom patientDto = new UserCreateFrom();
                patientDto.setUsername("p"); // login username
                patientDto.setPassword("p"); // login password
                patientDto.setRole("PATIENT");
                userAccountService.createUser(patientDto);
                System.out.println("Patient user account created!");
            } catch (IllegalArgumentException ignored) {
                System.out.println("Patient user already exists");
            }

            try {
                UserCreateFrom doctorDto = new UserCreateFrom();
                doctorDto.setUsername("d");
                doctorDto.setPassword("d");
                doctorDto.setRole("DOCTOR");
                userAccountService.createUser(doctorDto);
                System.out.println("Doctor user created!");
            } catch (IllegalArgumentException ignored) {
                System.out.println("Doctor already exists");
            }


            try {
                UserCreateFrom receptionDto = new UserCreateFrom();
                receptionDto.setUsername("r");
                receptionDto.setPassword("r");
                receptionDto.setRole("RECEPTIONIST");
                userAccountService.createUser(receptionDto);
                System.out.println("Receptionist user created!");
            } catch (IllegalArgumentException ignored) {
                System.out.println("Receptionist already exists");
            }
        };
    }
}

