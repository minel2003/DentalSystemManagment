package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Repository.PatientRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Getter
    private List<Patient> patients;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        initPatients();
    }

    public void initPatients() {
        patients = new ArrayList<>();
        patients = patientRepository.findAll();

    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public void save(Patient patient) {
        patientRepository.save(patient);
    }


    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id));
    }
    public Patient findByUsername(String username) {
        return patientRepository.findByUsername(username).orElse(null);
    }
    public Patient findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    /**
     * Find patient by UserAccount username (which is typically the email)
     */
    public Patient findByUserAccountUsername(String username) {
        // First try by email (since UserAccount username is usually email)
        Patient patient = findByEmail(username);
        if (patient != null) {
            return patient;
        }
        // Fallback to username field
        return findByUsername(username);
    }
}
