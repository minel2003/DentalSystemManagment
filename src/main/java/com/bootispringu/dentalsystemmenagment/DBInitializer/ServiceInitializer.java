package com.bootispringu.dentalsystemmenagment.DBInitializer;

import com.bootispringu.dentalsystemmenagment.Entity.Service;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import com.bootispringu.dentalsystemmenagment.Service.ServiceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class ServiceInitializer {

    @Bean
    CommandLineRunner initServices(ServiceService serviceService) {
        return args -> {

            createServiceIfNotExists(serviceService, "Tooth Extraction", 
                    "Surgical or simple extraction of a tooth", new BigDecimal("150.00"));
            
            createServiceIfNotExists(serviceService, "Wisdom Tooth Removal", 
                    "Surgical removal of impacted or problematic wisdom teeth", new BigDecimal("300.00"));
            
            createServiceIfNotExists(serviceService, "Dental Implant Placement", 
                    "Surgical placement of a dental implant in the jawbone", new BigDecimal("1500.00"));
            
            createServiceIfNotExists(serviceService, "Bone Grafting", 
                    "Bone augmentation procedure to strengthen the jawbone for implants", new BigDecimal("800.00"));
            
            createServiceIfNotExists(serviceService, "Apicoectomy (Root End Surgery)", 
                    "Surgical removal of the tip of a tooth root and surrounding infected tissue", new BigDecimal("400.00"));
            
            System.out.println("Oral surgery services initialized!");
        };
    }

    private void createServiceIfNotExists(ServiceService serviceService, String name, String description, BigDecimal price) {
        try {
            if (serviceService.findByName(name).isEmpty()) {
                Service service = new Service();
                service.setName(name);
                service.setDescription(description);
                service.setPrice(price);
                service.setStatus(Status.ACTIVE);
                serviceService.save(service);
                System.out.println("Created service: " + name);
            } else {
                System.out.println("Service already exists: " + name);
            }
        } catch (Exception e) {
            System.err.println("Failed to create service " + name + ": " + e.getMessage());
        }
    }
}

