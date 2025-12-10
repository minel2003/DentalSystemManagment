package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Role;
import com.bootispringu.dentalsystemmenagment.Repository.EmployeeRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    @Getter
    private List<Employee> employees;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public List<Employee> findAllDoctors() {
        return employeeRepository.findByRole(Role.DOCTOR);
    }
    
    public List<Employee> findAllReceptionists() {
        return employeeRepository.findByRole(Role.RECEPTIONIST);
    }
    
    public List<Employee> findAllManagers() {
        return employeeRepository.findByRole(Role.MANAGER);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
    }
    public Employee findByUserAccountUsername(String username) {

        return employeeRepository.findByEmail(username).orElse(null);
    }
    
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
