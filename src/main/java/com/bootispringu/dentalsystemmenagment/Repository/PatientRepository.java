package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByEmail(String email);
    Optional<Patient> findByUsername(String username);


}