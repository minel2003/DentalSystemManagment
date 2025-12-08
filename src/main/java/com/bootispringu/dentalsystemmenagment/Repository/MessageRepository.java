package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Message;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Get all messages sent by a patient
    List<Message> findByPatient(Patient patient);

    // Optional: Get all messages sent to a specific doctor
    List<Message> findByDoctor(Employee doctor);
    List<Message> findByDoctorOrderBySentAtDesc(Employee doctor);

    long countByDoctor(Employee doctor);

}
