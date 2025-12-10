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


    @GetMapping("/patient/home")
    public String patientHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }


        Patient patient = patientService.findByUserAccountUsername(userDetails.getUsername());


        if (patient == null) {
            model.addAttribute("patient", null);
            model.addAttribute("upcomingAppointment", null);
            model.addAttribute("completedTreatments", 0L);
            model.addAttribute("feedbackCount", 0L);
            model.addAttribute("profileIncomplete", true);
            return "patient/home";
        }


        Appointment upcomingAppointment =
                appointmentService.getUpcomingAppointmentForPatient(patient);


        long completedTreatments =
                appointmentService.countCompletedAppointments(patient);


        long feedbackCount =
                feedbackService.countFeedbacksByPatient(patient);


        model.addAttribute("patient", patient);
        model.addAttribute("upcomingAppointment", upcomingAppointment);
        model.addAttribute("completedTreatments", completedTreatments);
        model.addAttribute("feedbackCount", feedbackCount);
        model.addAttribute("profileIncomplete", false);

        return "patient/home";
    }


    @GetMapping("/patient/appointments")
    public String viewAppointments(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Patient patient = patientService.findByUserAccountUsername(userDetails.getUsername());
        if (patient == null) {

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


    @GetMapping("/patient/feedback")
    public String feedback(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("featureName", "Submit Feedback");
        model.addAttribute("backUrl", "/patient/home");
        return "coming_soon";
    }


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
