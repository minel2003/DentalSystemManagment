package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);
    List<Employee> findByRole(Role role); // ✅ must match enum type
    Optional<Employee> findByEmail(String email);

}
