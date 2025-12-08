package com.bootispringu.dentalsystemmenagment.Controller;

import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Message;
import com.bootispringu.dentalsystemmenagment.Entity.Result;
import com.bootispringu.dentalsystemmenagment.Service.AppointmentService;
import com.bootispringu.dentalsystemmenagment.Service.EmployeeService;
import com.bootispringu.dentalsystemmenagment.Service.MessageService;
import com.bootispringu.dentalsystemmenagment.Service.ResultService;
import com.bootispringu.dentalsystemmenagment.dto.ResultFrom;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DoctorController {

    private final EmployeeService doctorService;
    private final AppointmentService appointmentService;
    private final MessageService messageService;
    private final ResultService resultService;

    public DoctorController(EmployeeService doctorService,
                            AppointmentService appointmentService,
                            MessageService messageService,
                            ResultService resultService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.messageService = messageService;
        this.resultService = resultService;
    }

    // ========================= HOME =========================
    @GetMapping("/doctor/home")
    public String doctorHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());

        // If profile missing, still load page
        if (doctor == null) {
            model.addAttribute("doctor", null);
            model.addAttribute("upcomingAppointments", 0);
            model.addAttribute("completedTreatments", 0);
            model.addAttribute("messagesCount", 0);
            model.addAttribute("profileIncomplete", true);
            return "doctor/home";
        }

        long upcomingAppointments =
                appointmentService.countUpcomingAppointmentsForDoctor(doctor);

        long completedTreatments =
                appointmentService.countCompletedAppointmentsForDoctor(doctor);

        long messagesCount =
                messageService.countMessagesForDoctor(doctor);

        model.addAttribute("doctor", doctor);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("completedTreatments", completedTreatments);
        model.addAttribute("messagesCount", messagesCount);
        model.addAttribute("profileIncomplete", false);

        return "doctor/home";
    }

    // ========================= APPOINTMENTS =========================
    @GetMapping("/doctor/appointments")
    public String viewDoctorAppointments(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            model.addAttribute("appointments", List.of());
            model.addAttribute("profileIncomplete", true);
            return "doctor/appointments";
        }

        List<Appointment> appointments =
                appointmentService.getAppointmentsForDoctor(doctor);

        // Check which appointments already have results
        java.util.Map<Long, Boolean> appointmentHasResult = new java.util.HashMap<>();
        for (Appointment appointment : appointments) {
            boolean hasResult = resultService.hasResultForPatientAndDoctor(
                    appointment.getPatient(),
                    appointment.getDoctor()
            );
            appointmentHasResult.put(appointment.getId(), hasResult);
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("appointmentHasResult", appointmentHasResult);
        model.addAttribute("profileIncomplete", false);

        return "doctor/appointments";
    }

    // ========================= MESSAGES =========================
    @GetMapping("/doctor/messages")
    public String viewMessages(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            model.addAttribute("messages", List.of());
            model.addAttribute("profileIncomplete", true);
            return "doctor/messages";
        }

        List<Message> messages = messageService.getMessagesForDoctor(doctor);

        model.addAttribute("messages", messages);
        model.addAttribute("profileIncomplete", false);

        return "doctor/messages";
    }

    // ========================= ADD RESULT =========================
    @GetMapping("/doctor/result/add")
    public String showAddResultForm(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam("appointmentId") Long appointmentId,
                                    Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            return "redirect:/doctor/home";
        }

        Appointment appointment = appointmentService.findById(appointmentId);

        // Verify that this appointment belongs to the logged-in doctor
        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/appointments";
        }

        ResultFrom resultForm = new ResultFrom();
        resultForm.setAppointmentId(appointmentId);

        model.addAttribute("resultForm", resultForm);
        model.addAttribute("appointment", appointment);
        model.addAttribute("patient", appointment.getPatient());

        return "doctor/result_add";
    }

    @PostMapping("/doctor/result/add")
    public String saveResult(@AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute("resultForm") @Valid ResultFrom resultForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            return "redirect:/doctor/home";
        }

        if (bindingResult.hasErrors()) {
            Appointment appointment = appointmentService.findById(resultForm.getAppointmentId());
            model.addAttribute("appointment", appointment);
            model.addAttribute("patient", appointment.getPatient());
            return "doctor/result_add";
        }

        Appointment appointment = appointmentService.findById(resultForm.getAppointmentId());

        // Verify that this appointment belongs to the logged-in doctor
        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid appointment.");
            return "redirect:/doctor/appointments";
        }

        resultService.createResultFromAppointment(
                appointment,
                resultForm.getTreatment(),
                resultForm.getNotes()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Result added successfully!");
        return "redirect:/doctor/appointments";
    }

    // ========================= VIEW RESULTS =========================
    @GetMapping("/doctor/results")
    public String viewResults(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            model.addAttribute("results", List.of());
            model.addAttribute("profileIncomplete", true);
            return "doctor/results";
        }

        List<Result> results = resultService.getResultsForDoctor(doctor);
        model.addAttribute("results", results);
        model.addAttribute("profileIncomplete", false);

        return "doctor/results";
    }
}
