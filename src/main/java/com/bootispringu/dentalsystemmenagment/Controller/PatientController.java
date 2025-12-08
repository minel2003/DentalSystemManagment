package com.bootispringu.dentalsystemmenagment.Controller;


import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Entity.Result;
import com.bootispringu.dentalsystemmenagment.Service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final FeedbackService feedbackService;
    private final ResultService resultService;

    public PatientController(PatientService patientService,
                             AppointmentService appointmentService,
                             FeedbackService feedbackService,
                             ResultService resultService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.feedbackService = feedbackService;
        this.resultService = resultService;
    }

    // ========================= Home =========================
    @GetMapping("/patient/home")
    public String patientHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        // Fetch logged-in patient (UserAccount username is typically email)
        Patient patient = patientService.findByUserAccountUsername(userDetails.getUsername());

        // If patient profile doesn't exist yet, still allow access but with limited data
        if (patient == null) {
            model.addAttribute("patient", null);
            model.addAttribute("upcomingAppointment", null);
            model.addAttribute("completedTreatments", 0L);
            model.addAttribute("feedbackCount", 0L);
            model.addAttribute("profileIncomplete", true);
            return "patient/home";
        }

        // Upcoming appointment
        Appointment upcomingAppointment =
                appointmentService.getUpcomingAppointmentForPatient(patient);

        // Completed treatments count
        long completedTreatments =
                appointmentService.countCompletedAppointments(patient);

        // Feedback count
        long feedbackCount =
                feedbackService.countFeedbacksByPatient(patient);

        // Add data to model
        model.addAttribute("patient", patient);
        model.addAttribute("upcomingAppointment", upcomingAppointment);
        model.addAttribute("completedTreatments", completedTreatments);
        model.addAttribute("feedbackCount", feedbackCount);
        model.addAttribute("profileIncomplete", false);

        return "patient/home";
    }

    // ========================= APPOINTMENTS =========================
    @GetMapping("/patient/appointments")
    public String viewAppointments(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Patient patient = patientService.findByUserAccountUsername(userDetails.getUsername());
        if (patient == null) {
            // If patient profile doesn't exist, show empty appointments list
            model.addAttribute("appointments", new ArrayList<>());
            model.addAttribute("profileIncomplete", true);
            return "patient/appointments";
        }

        List<Appointment> appointments =
                appointmentService.getAppointmentsForPatient(patient);

        model.addAttribute("appointments", appointments);
        model.addAttribute("profileIncomplete", false);

        return "patient/appointments";
    }
    // ========================= VIEW RESULTS =========================
    @GetMapping("/patient/results")
    public String viewResults(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Patient patient = patientService.findByUserAccountUsername(userDetails.getUsername());
        if (patient == null) {
            model.addAttribute("results", new ArrayList<>());
            model.addAttribute("profileIncomplete", true);
            return "patient/results";
        }

        List<Result> results = resultService.getResultsForPatient(patient);
        model.addAttribute("results", results);
        model.addAttribute("profileIncomplete", false);

        return "patient/results";
    }

    // ========================= FEEDBACK =========================
    @GetMapping("/patient/feedback")
    public String feedback(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("featureName", "Submit Feedback");
        model.addAttribute("backUrl", "/patient/home");
        return "coming_soon";
    }

    // ========================= MESSAGE =========================
    @GetMapping("/patient/message")
    public String message(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("featureName", "Send Message to Doctor");
        model.addAttribute("backUrl", "/patient/home");
        return "coming_soon";
    }
}
