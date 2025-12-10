package com.bootispringu.dentalsystemmenagment.Controller;


import com.bootispringu.dentalsystemmenagment.Entity.*;
import com.bootispringu.dentalsystemmenagment.Service.AppointmentService;
import com.bootispringu.dentalsystemmenagment.Service.AppointmentServiceService;
import com.bootispringu.dentalsystemmenagment.Service.EmployeeService;
import com.bootispringu.dentalsystemmenagment.Service.PatientService;
import com.bootispringu.dentalsystemmenagment.Service.ReceiptService;
import com.bootispringu.dentalsystemmenagment.Service.UserAccountService;
import com.bootispringu.dentalsystemmenagment.dto.AppointmentForm;
import com.bootispringu.dentalsystemmenagment.dto.PatientForm;
import com.bootispringu.dentalsystemmenagment.dto.ReceiptForm;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.Valid;
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
public class ReceptionistController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final EmployeeService employeeService;
    private final UserAccountService userAccountService;
    private final ReceiptService receiptService;
    private final AppointmentServiceService appointmentServiceService;

    public ReceptionistController(PatientService patientService,
                                  AppointmentService appointmentService,
                                  EmployeeService employeeService,
                                  UserAccountService userAccountService,
                                  ReceiptService receiptService,
                                  AppointmentServiceService appointmentServiceService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.employeeService = employeeService;
        this.userAccountService = userAccountService;
        this.receiptService = receiptService;
        this.appointmentServiceService = appointmentServiceService;
    }


    @GetMapping("/receptionist/home")
    public String receptionistHome(Model model, org.springframework.security.web.csrf.CsrfToken csrfToken) {
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        return "receptionist/home";
    }


    @GetMapping("/receptionist/patient/add")
    public String addPatient(Model model) {
        model.addAttribute("patientForm", new PatientForm());
        return "receptionist/patient_add";
    }

    @PostMapping("/receptionist/patient/add")
    public String savePatient(@ModelAttribute("patientForm") @Valid PatientForm patientForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model,
                              @RequestParam(value = "redirectTo", required = false) String redirectTo) {
        if (bindingResult.hasErrors()) {

            if (redirectTo != null && redirectTo.equals("/receptionist/appointment/create")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors in the form below.");
                return "redirect:" + redirectTo;
            }

            model.addAttribute("errorMessage", "Please fix the validation errors in the form below.");
            return "receptionist/patient_add";
        }

        try {
            Patient patient = new Patient();
            patient.setPatientId(patientForm.getPatientId());
            patient.setPatientPersonalNumber(patientForm.getPatientPersonalNumber());
            patient.setFirstName(patientForm.getFirstName());
            patient.setFatherName(patientForm.getFatherName());
            patient.setLastName(patientForm.getLastName());
            patient.setPhoneNumber(patientForm.getPhoneNumber());
            patient.setEmail(patientForm.getEmail());
            patient.setAddress(patientForm.getAddress());
            patient.setCity(patientForm.getCity());
            patient.setState(patientForm.getState());
            patient.setBirthDate(patientForm.getBirthDate());
            patient.setGender(patientForm.getGender());
            patient.setMedicalHistory(patientForm.getMedicalHistory());
            patient.setAlergies(patientForm.getAlergies());
            patient.setCurrentMedications(patientForm.getCurrentMedications());
            patient.setBloodType(patientForm.getBloodType());


            patient.setStatus(Status.ACTIVE);
            patientService.save(patient);


            String loginUsername = patient.getEmail() != null ? patient.getEmail() : "generated";
            try {
                UserAccount userAccount = userAccountService.createUserAccountForPatient(patient);
                loginUsername = userAccount.getUsername();
            } catch (Exception e) {

                System.err.println("Failed to create user account for patient: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("successMessage", "Patient added successfully! Login username: " +
                    loginUsername + ", Default password: 1234");


            if (redirectTo != null && redirectTo.equals("/receptionist/appointment/create")) {
                return "redirect:" + redirectTo;
            }

            return "redirect:/receptionist/patient/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create patient: " + e.getMessage());
            return "redirect:/receptionist/patient/add";
        }
    }

    @GetMapping("/receptionist/patient/list")
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.findAll());
        return "receptionist/patient_list";
    }

    @GetMapping("/receptionist/patient/edit/{id}")
    public String editPatient(@PathVariable Long id, Model model) {
        model.addAttribute("featureName", "Edit Patient");
        model.addAttribute("backUrl", "/receptionist/patient/list");
        return "coming_soon";
    }

    @GetMapping("/receptionist/patient/delete/{id}")
    public String deletePatient(@PathVariable Long id, Model model) {
        model.addAttribute("featureName", "Delete Patient");
        model.addAttribute("backUrl", "/receptionist/patient/list");
        return "coming_soon";
    }


    @GetMapping("/receptionist/doctor/list")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", employeeService.findAllDoctors());
        return "receptionist/doctor_list";
    }


    @GetMapping("/receptionist/appointment/create")
    public String createAppointment(Model model) {
        model.addAttribute("appointmentForm", new AppointmentForm());
        model.addAttribute("patientForm", new PatientForm());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctors", employeeService.findAllDoctors());
        return "receptionist/appointment_create";
    }

    @PostMapping("/receptionist/appointment/create")
    public String saveAppointment(@ModelAttribute("appointmentForm") @Valid AppointmentForm appointmentForm,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("patientForm", new PatientForm());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("doctors", employeeService.findAllDoctors());
            

            StringBuilder errorMsg = new StringBuilder("Please fix the following errors: ");
            bindingResult.getFieldErrors().forEach(error -> {
                errorMsg.append(error.getDefaultMessage()).append("; ");
            });
            model.addAttribute("errorMessage", errorMsg.toString());
            
            return "receptionist/appointment_create";
        }

        try {

            if (appointmentForm.getPatientId() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a patient!");
                return "redirect:/receptionist/appointment/create";
            }
            
            if (appointmentForm.getDoctorId() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a doctor!");
                return "redirect:/receptionist/appointment/create";
            }
            
            if (appointmentForm.getDate() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a date!");
                return "redirect:/receptionist/appointment/create";
            }
            
            if (appointmentForm.getTime() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a time!");
                return "redirect:/receptionist/appointment/create";
            }

            Patient patient;
            Employee doctor;
            
            try {
                patient = patientService.findById(appointmentForm.getPatientId());
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected patient not found! Please select a valid patient.");
                return "redirect:/receptionist/appointment/create";
            }
            
            try {
                doctor = employeeService.findById(appointmentForm.getDoctorId());
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected doctor not found! Please select a valid doctor.");
                return "redirect:/receptionist/appointment/create";
            }

            if (patient == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected patient not found!");
                return "redirect:/receptionist/appointment/create";
            }

            if (doctor == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected doctor not found!");
                return "redirect:/receptionist/appointment/create";
            }

            Appointment appointment = new Appointment();
            appointment.setAppointmentDate(appointmentForm.getDate());
            appointment.setAppointmentTime(appointmentForm.getTime());
            appointment.setNotes(appointmentForm.getNotes());
            appointment.setStatus(Status.SCHEDULED);
            appointment.setPatient(patient);
            appointment.setDoctor(doctor);

            appointmentService.save(appointment);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment created successfully!");
            return "redirect:/receptionist/appointment/create";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation error: " + e.getMessage());
            return "redirect:/receptionist/appointment/create";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create appointment: " + e.getMessage() + ". Please check that patient and doctor exist.");
            return "redirect:/receptionist/appointment/create";
        }
    }

    @GetMapping("/receptionist/appointment/list")
    public String showAppointments(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        

        java.util.Map<Long, Boolean> appointmentHasServices = new java.util.HashMap<>();
        java.util.Map<Long, Boolean> appointmentHasReceipt = new java.util.HashMap<>();
        java.util.Map<Long, java.math.BigDecimal> appointmentTotal = new java.util.HashMap<>();
        
        for (Appointment appointment : appointments) {
            List<com.bootispringu.dentalsystemmenagment.Entity.AppointmentService> services = 
                    appointmentServiceService.findByAppointment(appointment);
            appointmentHasServices.put(appointment.getId(), !services.isEmpty());
            appointmentTotal.put(appointment.getId(), 
                    appointmentServiceService.calculateTotalForAppointment(appointment));
            

            boolean hasReceipt = receiptService.findAll().stream()
                    .anyMatch(r -> r.getAppointment() != null && r.getAppointment().getId().equals(appointment.getId()));
            appointmentHasReceipt.put(appointment.getId(), hasReceipt);
        }
        
        model.addAttribute("appointments", appointments);
        model.addAttribute("appointmentHasServices", appointmentHasServices);
        model.addAttribute("appointmentHasReceipt", appointmentHasReceipt);
        model.addAttribute("appointmentTotal", appointmentTotal);
        return "receptionist/appointment_list";
    }

    @GetMapping("/receptionist/appointment/{appointmentId}/receipt/generate")
    public String generateReceiptFromAppointment(@PathVariable Long appointmentId,
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.findById(appointmentId);
            if (appointment == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found!");
                return "redirect:/receptionist/appointment/list";
            }


            List<com.bootispringu.dentalsystemmenagment.Entity.AppointmentService> services = 
                    appointmentServiceService.findByAppointment(appointment);
            if (services.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Cannot generate receipt: Appointment has no services. Doctor must add services first.");
                return "redirect:/receptionist/appointment/list";
            }


            boolean hasReceipt = receiptService.findAll().stream()
                    .anyMatch(r -> r.getAppointment() != null && r.getAppointment().getId().equals(appointment.getId()));
            if (hasReceipt) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Receipt already exists for this appointment.");
                return "redirect:/receptionist/appointment/list";
            }


            Employee receptionist = null;
            if (userDetails != null) {
                receptionist = employeeService.findByUserAccountUsername(userDetails.getUsername());
            }

            Receipt receipt = receiptService.createReceiptFromAppointment(appointment, receptionist);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Receipt generated successfully! Receipt Number: " + receipt.getReceiptNumber());
            return "redirect:/receptionist/appointment/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to generate receipt: " + e.getMessage());
            return "redirect:/receptionist/appointment/list";
        }
    }


    @PostMapping("/receptionist/sync-accounts")
    public String syncUserAccounts(RedirectAttributes redirectAttributes) {
        try {
            UserAccountService.SyncResult result = userAccountService.syncMissingUserAccounts();

            String message = String.format("Sync completed! Created %d patient account(s) and %d doctor account(s) with default password '1234'.",
                    result.getPatientsCreated(), result.getDoctorsCreated());

            if (result.hasErrors()) {
                message += " Some errors occurred: " + String.join(", ", result.getErrors());
                redirectAttributes.addFlashAttribute("warningMessage", message);
            } else {
                redirectAttributes.addFlashAttribute("successMessage", message);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to sync user accounts: " + e.getMessage());
        }

        return "redirect:/receptionist/home";
    }


    
    @GetMapping("/receptionist/receipt/create")
    public String createReceipt(Model model) {
        ReceiptForm form = new ReceiptForm();
        form.setReceiptDate(java.time.LocalDate.now());
        form.setStatus("PAID");
        
        model.addAttribute("receiptForm", form);
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("appointments", appointmentService.findAll());
        return "receptionist/receipt_create";
    }

    @PostMapping("/receptionist/receipt/create")
    public String saveReceipt(@ModelAttribute("receiptForm") @Valid ReceiptForm receiptForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("appointments", appointmentService.findAll());
            
            StringBuilder errorMsg = new StringBuilder("Please fix the following errors: ");
            bindingResult.getFieldErrors().forEach(error -> {
                errorMsg.append(error.getDefaultMessage()).append("; ");
            });
            model.addAttribute("errorMessage", errorMsg.toString());
            return "receptionist/receipt_create";
        }

        try {
            Patient patient = patientService.findById(receiptForm.getPatientId());
            if (patient == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected patient not found!");
                return "redirect:/receptionist/receipt/create";
            }

            Appointment appointment = null;
            if (receiptForm.getAppointmentId() != null) {
                try {
                    appointment = appointmentService.findById(receiptForm.getAppointmentId());
                } catch (Exception e) {

                }
            }


            Employee receptionist = null;
            if (userDetails != null) {
                receptionist = employeeService.findByUserAccountUsername(userDetails.getUsername());
            }

            Receipt receipt = new Receipt();
            receipt.setPatient(patient);
            receipt.setAppointment(appointment);
            receipt.setReceiptDate(receiptForm.getReceiptDate());
            receipt.setAmount(receiptForm.getAmount());
            receipt.setPaymentMethod(receiptForm.getPaymentMethod());
            receipt.setDescription(receiptForm.getDescription());
            receipt.setStatus(Receipt.ReceiptStatus.valueOf(receiptForm.getStatus().toUpperCase()));
            receipt.setCreatedBy(receptionist);

            Receipt savedReceipt = receiptService.save(receipt);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Receipt created successfully! Receipt Number: " + savedReceipt.getReceiptNumber());
            return "redirect:/receptionist/appointment/list";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to create receipt: " + e.getMessage());
            return "redirect:/receptionist/receipt/create";
        }
    }

}
