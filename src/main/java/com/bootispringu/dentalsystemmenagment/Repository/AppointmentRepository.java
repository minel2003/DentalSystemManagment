package com.bootispringu.dentalsystemmenagment.Repository;

import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


        // All appointments for a patient
        List<Appointment> findByPatient(Patient patient);

        // Count completed appointments
        long countByPatientAndStatus(Patient patient, Status status);

        // Find next upcoming appointment for a patient
        @Query("SELECT a FROM Appointment a " +
                "WHERE a.patient = :patient " +
                "AND (a.appointmentDate > :today OR (a.appointmentDate = :today AND a.appointmentTime > :now)) " +
                "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
        Appointment findNextAppointmentForPatient(@Param("patient") Patient patient,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now);
//Doctor
        List<Appointment> findByDoctorOrderByAppointmentDateAscAppointmentTimeAsc(Employee doctor);


        // Count upcoming appointments for doctor
        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND " +
                "(a.appointmentDate > :today OR (a.appointmentDate = :today AND a.appointmentTime >= :now)) AND a.status = 'ACTIVE'")
        long countUpcomingAppointmentsForDoctor(@Param("doctor") Employee doctor,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now);

        // Count completed appointments for doctor
        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'COMPLETED'")
        long countCompletedAppointmentsForDoctor(@Param("doctor") Employee doctor);
    }

