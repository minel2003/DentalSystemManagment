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
                UserCreateFrom managerDto = new UserCreateFrom();
                managerDto.setUsername("m");
                managerDto.setPassword("m");
                managerDto.setRole("MANAGER");
                userAccountService.createUser(managerDto);
                System.out.println("Manager user created!");
            } catch (IllegalArgumentException ignored) {
                System.out.println("Manager already exists");
            }


            try {
                UserCreateFrom financeDto = new UserCreateFrom();
                financeDto.setUsername("f");
                financeDto.setPassword("f");
                financeDto.setRole("FINANCE");
                userAccountService.createUser(financeDto);
                System.out.println("Finance user created!");
            } catch (IllegalArgumentException ignored) {
                System.out.println("Finance already exists");
            }
        };
    }
}

