package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Department;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByStatus(Status status);
    
    @Query("SELECT d FROM Department d WHERE d.departmentType.departmentTypeId = :departmentTypeId")
    List<Department> findByDepartmentType_DepartmentTypeId(@Param("departmentTypeId") Long departmentTypeId);
    
    @Query("SELECT d FROM Department d WHERE d.departmentType.departmentTypeId = :departmentTypeId AND d.status = :status")
    List<Department> findByDepartmentType_DepartmentTypeIdAndStatus(@Param("departmentTypeId") Long departmentTypeId, @Param("status") Status status);
}
