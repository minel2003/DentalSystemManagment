package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Entity.Result;
import com.bootispringu.dentalsystemmenagment.Repository.ResultRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResultService {


    private final ResultRepository resultRepository;

    @Getter
    private List<Result> resultList;

    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    // Save a new result
    public Result saveResult(Result result) {
        return resultRepository.save(result);
    }

    // Get all results for a patient
    public List<Result> getResultsForPatient(Patient patient) {
        return resultRepository.findByPatientOrderByCreatedAtDesc(patient);
    }

    // Get latest result for a patient
    public Result getLatestResultForPatient(Patient patient) {
        return resultRepository.findTopByPatientOrderByCreatedAtDesc(patient);
    }

    // Get all results for a doctor
    public List<Result> getResultsForDoctor(Employee doctor) {
        return resultRepository.findByDoctorOrderByCreatedAtDesc(doctor);
    }

    // Create result from appointment
    public Result createResultFromAppointment(Appointment appointment, String treatment, String notes) {
        Result result = new Result();
        result.setPatient(appointment.getPatient());
        result.setDoctor(appointment.getDoctor());
        result.setTreatment(treatment);
        result.setNotes(notes);
        return saveResult(result);
    }

    /**
     * Check if a result already exists for a specific patient and doctor combination
     * (used to check if appointment already has a result)
     */
    public boolean hasResultForPatientAndDoctor(Patient patient, Employee doctor) {
        List<Result> results = resultRepository.findByPatient(patient);
        return results.stream()
                .anyMatch(result -> result.getDoctor() != null && result.getDoctor().getId().equals(doctor.getId()));
    }
}
