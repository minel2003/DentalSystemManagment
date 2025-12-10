package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {


    List<Result> findByPatient(Patient patient);


    List<Result> findByPatientOrderByCreatedAtDesc(Patient patient);


    List<Result> findByDoctorOrderByCreatedAtDesc(Employee doctor);


    Result findTopByPatientOrderByCreatedAtDesc(Patient patient);
}