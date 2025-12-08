package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Department;
import com.bootispringu.dentalsystemmenagment.Repository.DepartmentRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {


        private final DepartmentRepository departmentRepository;

        @Getter
        private List<Department> departments;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

        public List<Department> findAll () {
        return departmentRepository.findAll();
    }

    }

