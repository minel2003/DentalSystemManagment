package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import com.bootispringu.dentalsystemmenagment.Repository.AppointmentRepository;
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

    // Save appointment
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // Find appointment by ID
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    // Optional: find all appointments
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsForPatient(Patient patient) {
        return appointmentRepository.findByPatient(patient);
    }

    // Get the upcoming appointment for a patient
    public Appointment getUpcomingAppointmentForPatient(Patient patient) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return appointmentRepository.findNextAppointmentForPatient(patient, today, now);
    }

    // Count completed appointments
    public long countCompletedAppointments(Patient patient) {
        return appointmentRepository.countByPatientAndStatus(patient, Status.ACTIVE);
    }

    // ===== Doctor Methods =====
    public long countUpcomingAppointmentsForDoctor(Employee doctor) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return appointmentRepository.countUpcomingAppointmentsForDoctor(doctor, today, now);
    }

    public long countCompletedAppointmentsForDoctor(Employee doctor) {
        return appointmentRepository.countCompletedAppointmentsForDoctor(doctor);
    }

    public List<Appointment> getAppointmentsForDoctor(Employee doctor) {
        return appointmentRepository.findByDoctorOrderByAppointmentDateAscAppointmentTimeAsc(doctor);
    }
}
