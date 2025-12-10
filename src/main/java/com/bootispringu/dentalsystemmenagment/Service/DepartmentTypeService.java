package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.DepartmentType;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import com.bootispringu.dentalsystemmenagment.Repository.DepartmentTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentTypeService {

    private final DepartmentTypeRepository departmentTypeRepository;

    public DepartmentTypeService(DepartmentTypeRepository departmentTypeRepository) {
        this.departmentTypeRepository = departmentTypeRepository;
    }

    public List<DepartmentType> findAll() {
        return departmentTypeRepository.findAll();
    }

    public List<DepartmentType> findAllActive() {
        return departmentTypeRepository.findByStatus(Status.ACTIVE);
    }

    public Optional<DepartmentType> findById(Long id) {
        return departmentTypeRepository.findById(id);
    }

    public Optional<DepartmentType> findByName(String name) {
        return departmentTypeRepository.findByName(name);
    }

    @Transactional
    public DepartmentType save(DepartmentType departmentType) {
        if (departmentType.getCreatedAt() == null) {
            departmentType.setCreatedAt(LocalDateTime.now());
        }
        departmentType.setUpdatedAt(LocalDateTime.now());
        if (departmentType.getStatus() == null) {
            departmentType.setStatus(Status.ACTIVE);
        }
        return departmentTypeRepository.save(departmentType);
    }

    @Transactional
    public void delete(Long id) {
        departmentTypeRepository.deleteById(id);
    }

    @Transactional
    public DepartmentType update(Long id, DepartmentType updatedDepartmentType) {
        DepartmentType existing = departmentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department Type not found with ID: " + id));
        
        existing.setName(updatedDepartmentType.getName());
        existing.setDescription(updatedDepartmentType.getDescription());
        existing.setStatus(updatedDepartmentType.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        
        return departmentTypeRepository.save(existing);
    }
}

