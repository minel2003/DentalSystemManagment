package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import com.bootispringu.dentalsystemmenagment.Repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;
    @Getter
    private List<Appointment> appointments;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }


    @Transactional
    public Appointment save(Appointment appointment) {
        if (appointment.getPatient() == null || appointment.getDoctor() == null) {
            throw new IllegalArgumentException("Patient and Doctor must be set before saving appointment");
        }
        if (appointment.getPatient().getPatientId() == null) {
            throw new IllegalArgumentException("Patient must have a valid ID");
        }
        if (appointment.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Doctor must have a valid ID");
        }
        Appointment saved = appointmentRepository.save(appointment);

        appointmentRepository.flush();
        return saved;
    }


    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }


    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsForPatient(Patient patient) {
        if (patient == null || patient.getPatientId() == null) {
            return List.of();
        }

        return appointmentRepository.findByPatientId(patient.getPatientId());
    }


    public Appointment getUpcomingAppointmentForPatient(Patient patient) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return appointmentRepository.findNextAppointmentForPatient(patient, today, now);
    }


    public long countCompletedAppointments(Patient patient) {
        return appointmentRepository.countByPatientAndStatus(patient, Status.ACTIVE);
    }


    public long countUpcomingAppointmentsForDoctor(Employee doctor) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return appointmentRepository.countUpcomingAppointmentsForDoctor(doctor, today, now);
    }

    public long countCompletedAppointmentsForDoctor(Employee doctor) {
        return appointmentRepository.countCompletedAppointmentsForDoctor(doctor);
    }

    public List<Appointment> getAppointmentsForDoctor(Employee doctor) {
        if (doctor == null || doctor.getId() == null) {
            return List.of();
        }

        return appointmentRepository.findByDoctorId(doctor.getId());
    }
}
