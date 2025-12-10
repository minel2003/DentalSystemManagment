package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.DepartmentType;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentTypeRepository extends JpaRepository<DepartmentType, Long> {
    List<DepartmentType> findByStatus(Status status);
    Optional<DepartmentType> findByName(String name);
}

