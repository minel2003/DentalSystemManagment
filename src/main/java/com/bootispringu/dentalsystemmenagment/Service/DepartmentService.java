package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Department;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import com.bootispringu.dentalsystemmenagment.Repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Getter
    private List<Department> departments;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public List<Department> findAllActive() {
        return departmentRepository.findByStatus(Status.ACTIVE);
    }

    public List<Department> findByDepartmentType(Long departmentTypeId) {
        return departmentRepository.findByDepartmentType_DepartmentTypeId(departmentTypeId);
    }

    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    @Transactional
    public Department save(Department department) {
        if (department.getCreatedAt() == null) {
            department.setCreatedAt(LocalDateTime.now());
        }
        department.setUpdatedAt(LocalDateTime.now());
        if (department.getStatus() == null) {
            department.setStatus(Status.ACTIVE);
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public void delete(Long id) {
        departmentRepository.deleteById(id);
    }

    @Transactional
    public Department update(Long id, Department updatedDepartment) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
        
        existing.setName(updatedDepartment.getName());
        existing.setDescription(updatedDepartment.getDescription());
        existing.setLocation(updatedDepartment.getLocation());
        existing.setDepartmentType(updatedDepartment.getDepartmentType());
        existing.setCategory(updatedDepartment.getCategory());
        existing.setStatus(updatedDepartment.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        
        return departmentRepository.save(existing);
    }
}

