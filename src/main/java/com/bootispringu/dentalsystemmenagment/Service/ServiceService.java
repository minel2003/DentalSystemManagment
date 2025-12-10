package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Status;
import com.bootispringu.dentalsystemmenagment.Repository.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<com.bootispringu.dentalsystemmenagment.Entity.Service> findAll() {
        return serviceRepository.findAll();
    }

    public List<com.bootispringu.dentalsystemmenagment.Entity.Service> findAllActive() {
        return serviceRepository.findByStatusOrderByNameAsc(Status.ACTIVE);
    }

    public Optional<com.bootispringu.dentalsystemmenagment.Entity.Service> findById(Long id) {
        return serviceRepository.findById(id);
    }

    public Optional<com.bootispringu.dentalsystemmenagment.Entity.Service> findByName(String name) {
        return serviceRepository.findByName(name);
    }

    @Transactional
    public com.bootispringu.dentalsystemmenagment.Entity.Service save(com.bootispringu.dentalsystemmenagment.Entity.Service service) {
        if (service.getCreatedAt() == null) {
            service.setCreatedAt(LocalDateTime.now());
        }
        service.setUpdatedAt(LocalDateTime.now());
        if (service.getStatus() == null) {
            service.setStatus(Status.ACTIVE);
        }
        return serviceRepository.save(service);
    }

    @Transactional
    public void delete(Long id) {
        serviceRepository.deleteById(id);
    }
}

