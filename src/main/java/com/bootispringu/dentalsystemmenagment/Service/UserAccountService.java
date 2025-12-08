package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Entity.Role;
import com.bootispringu.dentalsystemmenagment.Entity.UserAccount;
import com.bootispringu.dentalsystemmenagment.Repository.UserAccountRepository;
import com.bootispringu.dentalsystemmenagment.dto.UserCreateFrom;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientService patientService;
    private final EmployeeService employeeService;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              PasswordEncoder passwordEncoder,
                              PatientService patientService,
                              EmployeeService employeeService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientService = patientService;
        this.employeeService = employeeService;
    }

    public List<UserAccount> findAll() {
        return userAccountRepository.findAll();
    }
    @Transactional
    public UserAccount createUser(UserCreateFrom dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {

            throw new IllegalArgumentException("Username is required");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        String normalizedRole = normalizeRole(dto.getRole());

        userAccountRepository.findByUsername(dto.getUsername().trim())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Username already exists");
                });
        try{
            UserAccount user = new UserAccount();
            user.setUsername(dto.getUsername().trim());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRole(Role.valueOf(normalizedRole));
            return userAccountRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Unable to save user");
        }
    }

    public String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "PATIENT";
        }
        String value = role.trim().toUpperCase();
        return switch (value) {
            case "ADMIN", "PATIENT","DOCTOR","RECEPTIONIST" -> value;
            default -> "PATIENT";
        };
    }

    /**
     * Create a UserAccount for a patient with default password "1234"
     */
    @Transactional
    public UserAccount createUserAccountForPatient(Patient patient) {
        // Use email as username (most reliable identifier)
        String username = patient.getEmail();
        if (username == null || username.isBlank()) {
            // Generate username from patient name and ID if email not available
            username = (patient.getFirstName() + "." + patient.getLastName() + patient.getPatientId()).toLowerCase().replaceAll("\\s+", "");
        }

        // Check if username already exists, if so append patient ID
        String finalUsername = username.trim();
        int counter = 1;
        while (userAccountRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = username.trim() + patient.getPatientId() + (counter > 1 ? counter : "");
            counter++;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(finalUsername);
        userAccount.setPassword(passwordEncoder.encode("1234")); // Default password
        userAccount.setRole(Role.PATIENT);
        userAccount.setPatient(patient);

        return userAccountRepository.save(userAccount);
    }

    /**
     * Create a UserAccount for an employee (doctor/receptionist) with default password "1234"
     */
    @Transactional
    public UserAccount createUserAccountForEmployee(Employee employee) {
        // Use email as username, or generate one if email not available
        String username = employee.getEmail();
        if (username == null || username.isBlank()) {
            // Generate username from employee name and ID if email not available
            username = (employee.getFirstName() + "." + employee.getLastName() + employee.getId()).toLowerCase().replaceAll("\\s+", "");
        }

        // Check if username already exists, if so append employee ID
        String finalUsername = username.trim();
        int counter = 1;
        while (userAccountRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = username.trim() + employee.getId() + (counter > 1 ? counter : "");
            counter++;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(finalUsername);
        userAccount.setPassword(passwordEncoder.encode("1234")); // Default password
        userAccount.setRole(employee.getRole());
        userAccount.setEmployee(employee);

        return userAccountRepository.save(userAccount);
    }

    /**
     * Sync UserAccounts for all existing patients and doctors that don't have accounts
     * @return Summary of created accounts
     */
    @Transactional
    public SyncResult syncMissingUserAccounts() {
        int patientsCreated = 0;
        int doctorsCreated = 0;
        List<String> errors = new ArrayList<>();

        // Sync patients
        List<Patient> patients = patientService.findAll();
        for (Patient patient : patients) {
            try {
                // Check if patient already has a UserAccount
                if (userAccountRepository.findByPatient(patient).isEmpty()) {
                    createUserAccountForPatient(patient);
                    patientsCreated++;
                }
            } catch (Exception e) {
                errors.add("Failed to create account for patient " + patient.getFirstName() + " " + patient.getLastName() + ": " + e.getMessage());
            }
        }

        // Sync doctors
        List<Employee> doctors = employeeService.findAllDoctors();
        for (Employee doctor : doctors) {
            try {
                // Check if doctor already has a UserAccount
                if (userAccountRepository.findByEmployee(doctor).isEmpty()) {
                    createUserAccountForEmployee(doctor);
                    doctorsCreated++;
                }
            } catch (Exception e) {
                errors.add("Failed to create account for doctor " + doctor.getFirstName() + " " + doctor.getLastName() + ": " + e.getMessage());
            }
        }

        return new SyncResult(patientsCreated, doctorsCreated, errors);
    }

    /**
     * Result class for sync operation
     */
    public static class SyncResult {
        private final int patientsCreated;
        private final int doctorsCreated;
        private final List<String> errors;

        public SyncResult(int patientsCreated, int doctorsCreated, List<String> errors) {
            this.patientsCreated = patientsCreated;
            this.doctorsCreated = doctorsCreated;
            this.errors = errors;
        }

        public int getPatientsCreated() {
            return patientsCreated;
        }

        public int getDoctorsCreated() {
            return doctorsCreated;
        }

        public List<String> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
}
