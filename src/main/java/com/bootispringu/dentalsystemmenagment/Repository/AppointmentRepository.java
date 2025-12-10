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



        List<Appointment> findByPatient(Patient patient);
        

        @Query("SELECT a FROM Appointment a WHERE a.patient.patientId = :patientId ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
        List<Appointment> findByPatientId(@Param("patientId") Long patientId);


        long countByPatientAndStatus(Patient patient, Status status);


        @Query("SELECT a FROM Appointment a " +
                "WHERE a.patient = :patient " +
                "AND (a.appointmentDate > :today OR (a.appointmentDate = :today AND a.appointmentTime > :now)) " +
                "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
        Appointment findNextAppointmentForPatient(@Param("patient") Patient patient,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now);
            

        List<Appointment> findByDoctorOrderByAppointmentDateAscAppointmentTimeAsc(Employee doctor);
        

        @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
        List<Appointment> findByDoctorId(@Param("doctorId") Long doctorId);



        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND " +
                "(a.appointmentDate > :today OR (a.appointmentDate = :today AND a.appointmentTime >= :now)) AND a.status = 'ACTIVE'")
        long countUpcomingAppointmentsForDoctor(@Param("doctor") Employee doctor,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now);


        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'COMPLETED'")
        long countCompletedAppointmentsForDoctor(@Param("doctor") Employee doctor);
    }

