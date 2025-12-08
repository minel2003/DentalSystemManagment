package com.bootispringu.dentalsystemmenagment.Controller;


import com.bootispringu.dentalsystemmenagment.Entity.*;
import com.bootispringu.dentalsystemmenagment.Service.AppointmentService;
import com.bootispringu.dentalsystemmenagment.Service.EmployeeService;
import com.bootispringu.dentalsystemmenagment.Service.PatientService;
import com.bootispringu.dentalsystemmenagment.Service.UserAccountService;
import com.bootispringu.dentalsystemmenagment.dto.AppointmentForm;
import com.bootispringu.dentalsystemmenagment.dto.EmployeeForm;
import com.bootispringu.dentalsystemmenagment.dto.PatientForm;
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

    public ReceptionistController(PatientService patientService,
                                  AppointmentService appointmentService,
                                  EmployeeService employeeService,
                                  UserAccountService userAccountService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.employeeService = employeeService;
        this.userAccountService = userAccountService;
    }

    // ================== HOME ==================
    @GetMapping("/receptionist/home")
    public String receptionistHome(Model model, org.springframework.security.web.csrf.CsrfToken csrfToken) {
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        return "receptionist/home";
    }

    // ================== PATIENT ==================
    @GetMapping("/receptionist/patient/add")
    public String addPatient(Model model) {
        model.addAttribute("patientForm", new PatientForm());
        return "receptionist/patient_add";
    }

    @PostMapping("/receptionist/patient/add")
    public String savePatient(@ModelAttribute("patientForm") @Valid PatientForm patientForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(value = "redirectTo", required = false) String redirectTo) {
        if (bindingResult.hasErrors()) {
            // If redirectTo is provided, we're coming from appointment page
            if (redirectTo != null && redirectTo.equals("/receptionist/appointment/create")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please fix the errors below.");
                return "redirect:" + redirectTo;
            }
            return "receptionist/patient_add";
        }

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

        // ✅ Status enum directly
        patient.setStatus(Status.ACTIVE); // default status
        patientService.save(patient);

        // Create UserAccount for the patient with default password "1234"
        String loginUsername = patient.getEmail() != null ? patient.getEmail() : "generated";
        try {
            UserAccount userAccount = userAccountService.createUserAccountForPatient(patient);
            loginUsername = userAccount.getUsername();
        } catch (Exception e) {
            // If user account creation fails, log but don't fail the patient creation
            System.err.println("Failed to create user account for patient: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage", "Patient added successfully! Login username: " +
                loginUsername + ", Default password: 1234");

        // If redirectTo is provided, redirect back to appointment creation
        if (redirectTo != null && redirectTo.equals("/receptionist/appointment/create")) {
            return "redirect:" + redirectTo;
        }

        return "redirect:/receptionist/patient/add";
    }

    @GetMapping("/receptionist/patient/list")
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.findAll()); // fetch all patients
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

    // ================== DOCTOR ==================
    @GetMapping("/receptionist/doctor/add")
    public String addDoctor(Model model) {
        EmployeeForm form = new EmployeeForm();
        form.setRole(Role.DOCTOR); // pre-set role for the form
        model.addAttribute("employeeForm", form);
        return "receptionist/doctor_add";
    }

    @PostMapping("/receptionist/doctor/add")
    public String saveDoctor(@ModelAttribute("employeeForm") @Valid EmployeeForm employeeForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(value = "redirectTo", required = false) String redirectTo) {

        if (bindingResult.hasErrors()) {
            // If redirectTo is provided, we're coming from appointment page
            if (redirectTo != null && redirectTo.equals("/receptionist/appointment/create")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please fix the errors below.");
                return "redirect:" + redirectTo;
            }
            return "receptionist/doctor_add";
        }

        Employee doctor = new Employee();

        doctor.setFirstName(employeeForm.getFirstName());
        doctor.setFatherName(employeeForm.getFatherName());
        doctor.setLastName(employeeForm.getLastName());
        doctor.setPhoneNumber(employeeForm.getPhoneNumber());
        doctor.setEmail(employeeForm.getEmail());
        doctor.setAddress(employeeForm.getAddress());
        doctor.setCity(employeeForm.getCity());
        doctor.setState(employeeForm.getState());
        doctor.setBirthDate(employeeForm.getBirthDate());
        doctor.setGender(employeeForm.getGender());

        doctor.setRole(Role.DOCTOR);
        doctor.setSpecialization(employeeForm.getSpecialization());
        doctor.setHireDate(employeeForm.getHireDate());
        doctor.setSalary(employeeForm.getSalary());

        // FIX — status must be enum
        doctor.setStatus(Status.valueOf(employeeForm.getStatus().toUpperCase()));

        employeeService.save(doctor);

        // Create UserAccount for the doctor with default password "1234"
        String loginUsername = doctor.getEmail() != null ? doctor.getEmail() : "generated";
        try {
            userAccountService.createUserAccountForEmployee(doctor);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Doctor added successfully! You can now select them from the dropdown. Login username: " + loginUsername + ", Default password: 1234");
        } catch (Exception e) {
            // If user account creation fails, log but don't fail the doctor creation
            System.err.println("Failed to create user account for doctor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Doctor added successfully! You can now select them from the dropdown.");
        }

        // If redirectTo is provided, redirect back to appointment creation
        if (redirectTo != null && redirectTo.equals("/receptionist/appointment/create")) {
            return "redirect:" + redirectTo;
        }

        return "redirect:/receptionist/doctor/add";
    }


    @GetMapping("/receptionist/doctor/list")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", employeeService.findAllDoctors()); // fetch all doctors
        return "receptionist/doctor_list";
    }

    @GetMapping("/receptionist/doctor/edit/{id}")
    public String editDoctor(@PathVariable Long id, Model model) {
        model.addAttribute("featureName", "Edit Doctor");
        model.addAttribute("backUrl", "/receptionist/doctor/list");
        return "coming_soon";
    }

    @GetMapping("/receptionist/doctor/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, Model model) {
        model.addAttribute("featureName", "Delete Doctor");
        model.addAttribute("backUrl", "/receptionist/doctor/list");
        return "coming_soon";
    }

    // ================== APPOINTMENT ==================
    @GetMapping("/receptionist/appointment/create")
    public String createAppointment(Model model) {
        model.addAttribute("appointmentForm", new AppointmentForm());
        model.addAttribute("patientForm", new PatientForm()); // for patient registration modal
        EmployeeForm employeeForm = new EmployeeForm();
        employeeForm.setRole(Role.DOCTOR); // pre-set role for the form
        model.addAttribute("employeeForm", employeeForm); // for doctor registration modal
        model.addAttribute("patients", patientService.findAll()); // for dropdown
        model.addAttribute("doctors", employeeService.findAllDoctors()); // for dropdown
        return "receptionist/appointment_create";
    }

    @PostMapping("/receptionist/appointment/create")
    public String saveAppointment(@ModelAttribute("appointmentForm") @Valid AppointmentForm appointmentForm,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("patientForm", new PatientForm());
            EmployeeForm employeeForm = new EmployeeForm();
            employeeForm.setRole(Role.DOCTOR);
            model.addAttribute("employeeForm", employeeForm);
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("doctors", employeeService.findAllDoctors());
            return "receptionist/appointment_create";
        }

        Patient patient = patientService.findById(appointmentForm.getPatientId());
        Employee doctor = employeeService.findById(appointmentForm.getDoctorId());

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
    }

    @GetMapping("/receptionist/appointment/list")
    public String showAppointments(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointments);
        return "receptionist/appointment_list";
    }

    // ================== SYNC USER ACCOUNTS ==================
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

}
