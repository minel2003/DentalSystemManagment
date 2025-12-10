package com.bootispringu.dentalsystemmenagment.Controller;

import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Message;
import com.bootispringu.dentalsystemmenagment.Entity.Result;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import com.bootispringu.dentalsystemmenagment.Service.AppointmentService;
import com.bootispringu.dentalsystemmenagment.Service.AppointmentServiceService;
import com.bootispringu.dentalsystemmenagment.Service.EmployeeService;
import com.bootispringu.dentalsystemmenagment.Service.MessageService;
import com.bootispringu.dentalsystemmenagment.Service.ResultService;
import com.bootispringu.dentalsystemmenagment.Service.ServiceService;
import com.bootispringu.dentalsystemmenagment.dto.AppointmentServiceForm;
import com.bootispringu.dentalsystemmenagment.dto.ResultFrom;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ServiceService serviceService;
    private final AppointmentServiceService appointmentServiceService;

    public DoctorController(EmployeeService doctorService,
                            AppointmentService appointmentService,
                            MessageService messageService,
                            ResultService resultService,
                            ServiceService serviceService,
                            AppointmentServiceService appointmentServiceService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.messageService = messageService;
        this.resultService = resultService;
        this.serviceService = serviceService;
        this.appointmentServiceService = appointmentServiceService;
    }


    @GetMapping("/doctor/home")
    public String doctorHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());


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


        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/appointments";
        }

        ResultFrom resultForm = new ResultFrom();
        resultForm.setAppointmentId(appointmentId);


        List<com.bootispringu.dentalsystemmenagment.Entity.AppointmentService> appointmentServices = 
                appointmentServiceService.findByAppointment(appointment);
        java.math.BigDecimal total = appointmentServiceService.calculateTotalForAppointment(appointment);

        model.addAttribute("resultForm", resultForm);
        model.addAttribute("appointment", appointment);
        model.addAttribute("patient", appointment.getPatient());
        model.addAttribute("services", serviceService.findAllActive());
        model.addAttribute("appointmentServices", appointmentServices);
        model.addAttribute("appointmentTotal", total);
        model.addAttribute("serviceForm", new AppointmentServiceForm());

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
            List<com.bootispringu.dentalsystemmenagment.Entity.AppointmentService> appointmentServices = 
                    appointmentServiceService.findByAppointment(appointment);
            model.addAttribute("appointment", appointment);
            model.addAttribute("patient", appointment.getPatient());
            model.addAttribute("services", serviceService.findAllActive());
            model.addAttribute("appointmentServices", appointmentServices);
            model.addAttribute("serviceForm", new AppointmentServiceForm());
            return "doctor/result_add";
        }

        Appointment appointment = appointmentService.findById(resultForm.getAppointmentId());


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


    @GetMapping("/doctor/appointment/{id}/view")
    public String viewAppointment(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long id,
                                 Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            return "redirect:/doctor/home";
        }

        Appointment appointment = appointmentService.findById(id);
        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/appointments";
        }


        List<com.bootispringu.dentalsystemmenagment.Entity.AppointmentService> appointmentServices = 
                appointmentServiceService.findByAppointment(appointment);
        java.math.BigDecimal total = appointmentServiceService.calculateTotalForAppointment(appointment);

        model.addAttribute("appointment", appointment);
        model.addAttribute("patient", appointment.getPatient());
        model.addAttribute("services", serviceService.findAllActive());
        model.addAttribute("appointmentServices", appointmentServices);
        model.addAttribute("appointmentTotal", total);
        model.addAttribute("serviceForm", new AppointmentServiceForm());

        return "doctor/appointment_view";
    }


    @PostMapping("/doctor/appointment/{id}/status")
    public String updateAppointmentStatus(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable Long id,
                                         @RequestParam("status") String status,
                                         RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            return "redirect:/doctor/home";
        }

        try {
            Appointment appointment = appointmentService.findById(id);
            if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid appointment.");
                return "redirect:/doctor/appointments";
            }

            appointment.setStatus(Status.valueOf(status.toUpperCase()));
            appointmentService.save(appointment);

            redirectAttributes.addFlashAttribute("successMessage", 
                    "Appointment status updated to " + status + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update status: " + e.getMessage());
        }

        return "redirect:/doctor/appointment/" + id + "/view";
    }


    @PostMapping("/doctor/appointment/{appointmentId}/service/add")
    public String addServiceToAppointment(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable Long appointmentId,
                                         @ModelAttribute("serviceForm") @Valid AppointmentServiceForm serviceForm,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            return "redirect:/doctor/home";
        }

        Appointment appointment = appointmentService.findById(appointmentId);
        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid appointment.");
            return "redirect:/doctor/appointments";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide valid service information.");
            return "redirect:/doctor/result/add?appointmentId=" + appointmentId;
        }

        try {
            com.bootispringu.dentalsystemmenagment.Entity.Service service = 
                    serviceService.findById(serviceForm.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            appointmentServiceService.addServiceToAppointment(
                    appointment,
                    service,
                    serviceForm.getQuantity(),
                    serviceForm.getNotes()
            );

            redirectAttributes.addFlashAttribute("successMessage", 
                    "Service '" + service.getName() + "' added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to add service: " + e.getMessage());
        }

        return "redirect:/doctor/appointment/" + appointmentId + "/view";
    }

    @GetMapping("/doctor/appointment/{appointmentId}/service/delete/{serviceId}")
    public String removeServiceFromAppointment(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long appointmentId,
                                              @PathVariable Long serviceId,
                                              RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee doctor = doctorService.findByUserAccountUsername(userDetails.getUsername());
        if (doctor == null) {
            return "redirect:/doctor/home";
        }

        Appointment appointment = appointmentService.findById(appointmentId);
        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid appointment.");
            return "redirect:/doctor/appointments";
        }

        try {
            appointmentServiceService.delete(serviceId);
            redirectAttributes.addFlashAttribute("successMessage", "Service removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove service: " + e.getMessage());
        }

        return "redirect:/doctor/appointment/" + appointmentId + "/view";
    }
}
