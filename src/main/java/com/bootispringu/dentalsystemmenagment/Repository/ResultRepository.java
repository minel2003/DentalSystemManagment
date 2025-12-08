package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    // Get all results for a specific patient
    List<Result> findByPatient(Patient patient);

    // Get all results for a patient ordered by date (newest first)
    List<Result> findByPatientOrderByCreatedAtDesc(Patient patient);

    // Get all results for a doctor ordered by date (newest first)
    List<Result> findByDoctorOrderByCreatedAtDesc(Employee doctor);

    // Get the latest result for a patient (by createdAt descending)
    Result findTopByPatientOrderByCreatedAtDesc(Patient patient);
}