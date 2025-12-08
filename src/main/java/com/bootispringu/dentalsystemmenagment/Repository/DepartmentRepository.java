package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
