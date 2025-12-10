package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
    List<AppointmentService> findByAppointment(Appointment appointment);
    List<AppointmentService> findByAppointmentOrderByCreatedAtAsc(Appointment appointment);
}

