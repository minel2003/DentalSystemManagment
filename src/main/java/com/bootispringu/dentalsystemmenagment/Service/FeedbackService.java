package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Feedback;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Repository.FeedbackRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackService {

    private FeedbackRepository feedbackRepository;
    @Getter
    private List<Feedback> feedbackList;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }
    public List<Feedback> findAllFeedback() {
        return feedbackRepository.findAll();
    }
    // Save feedback
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    // Get all feedbacks for a patient
    public List<Feedback> getFeedbacksByPatient(Patient patient) {
        return feedbackRepository.findAll().stream()
                .filter(f -> f.getPatient().equals(patient))
                .toList();
    }

    // ✅ Count feedbacks submitted by a patient
    public long countFeedbacksByPatient(Patient patient) {
        return feedbackRepository.countByPatient(patient);
    }
}
