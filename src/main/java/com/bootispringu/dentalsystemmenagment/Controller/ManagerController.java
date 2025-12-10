package com.bootispringu.dentalsystemmenagment.Controller;

import com.bootispringu.dentalsystemmenagment.Entity.*;
import com.bootispringu.dentalsystemmenagment.Service.DepartmentService;
import com.bootispringu.dentalsystemmenagment.Service.EmployeeService;
import com.bootispringu.dentalsystemmenagment.Service.UserAccountService;
import com.bootispringu.dentalsystemmenagment.dto.EmployeeForm;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
public class ManagerController {

    private final EmployeeService employeeService;
    private final UserAccountService userAccountService;
    private final DepartmentService departmentService;

    public ManagerController(EmployeeService employeeService,
                            UserAccountService userAccountService,
                            DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.userAccountService = userAccountService;
        this.departmentService = departmentService;
    }


    @GetMapping("/manager/home")
    public String managerHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Employee manager = employeeService.findByUserAccountUsername(userDetails.getUsername());
        
        model.addAttribute("manager", manager);
        return "manager/home";
    }


    
    @GetMapping("/manager/staff/add")
    public String addStaff(Model model) {
        EmployeeForm form = new EmployeeForm();

        model.addAttribute("employeeForm", form);

        model.addAttribute("departments", departmentService.findAllActive());
        

        List<Role> availableRoles = Arrays.asList(Role.values());
        List<Role> excludedRoles = Arrays.asList(Role.ADMIN, Role.MANAGER, Role.PATIENT);
        List<Role> allowedRoles = availableRoles.stream()
                .filter(role -> !excludedRoles.contains(role))
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("availableRoles", allowedRoles);
        
        return "manager/staff_add";
    }

    @PostMapping("/manager/staff/add")
    public String saveStaff(@ModelAttribute("employeeForm") @Valid EmployeeForm employeeForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the validation errors in the form below.");
            model.addAttribute("departments", departmentService.findAllActive());
            

            List<Role> availableRoles = Arrays.asList(Role.values());
            List<Role> excludedRoles = Arrays.asList(Role.ADMIN, Role.MANAGER, Role.PATIENT);
            List<Role> allowedRoles = availableRoles.stream()
                    .filter(role -> !excludedRoles.contains(role))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("availableRoles", allowedRoles);
            
            return "manager/staff_add";
        }

        try {

            List<Role> excludedRoles = Arrays.asList(Role.ADMIN, Role.MANAGER, Role.PATIENT);
            if (employeeForm.getRole() == null || excludedRoles.contains(employeeForm.getRole())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You cannot create " + (employeeForm.getRole() != null ? employeeForm.getRole() : "null") + " accounts. Please select a valid role.");
                return "redirect:/manager/staff/add";
            }

            Employee employee = new Employee();
            employee.setFirstName(employeeForm.getFirstName());
            employee.setFatherName(employeeForm.getFatherName());
            employee.setLastName(employeeForm.getLastName());
            employee.setPhoneNumber(employeeForm.getPhoneNumber());
            employee.setEmail(employeeForm.getEmail());
            employee.setAddress(employeeForm.getAddress());
            employee.setCity(employeeForm.getCity());
            employee.setState(employeeForm.getState());
            employee.setBirthDate(employeeForm.getBirthDate());
            employee.setGender(employeeForm.getGender());
            employee.setRole(employeeForm.getRole());
            employee.setSpecialization(employeeForm.getSpecialization());
            employee.setHireDate(employeeForm.getHireDate());
            employee.setSalary(employeeForm.getSalary());
            employee.setStatus(Status.valueOf(employeeForm.getStatus().toUpperCase()));
            

            if (employeeForm.getDepartmentId() != null) {
                Department department = departmentService.findById(employeeForm.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found with ID: " + employeeForm.getDepartmentId()));
                employee.setDepartment(department);
            }

            employeeService.save(employee);


            String loginUsername = employee.getEmail() != null ? employee.getEmail() : "generated";
            try {
                userAccountService.createUserAccountForEmployee(employee);
                redirectAttributes.addFlashAttribute("successMessage",
                        employeeForm.getRole().name() + " added successfully! Login username: " + loginUsername + ", Default password: 1234");
            } catch (Exception e) {
                System.err.println("Failed to create user account for employee: " + e.getMessage());
                redirectAttributes.addFlashAttribute("successMessage",
                        employeeForm.getRole().name() + " added successfully!");
            }

            return "redirect:/manager/staff/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create " + employeeForm.getRole().name() + ": " + e.getMessage());
            return "redirect:/manager/staff/add";
        }
    }

    @GetMapping("/manager/staff/list")
    public String listStaff(Model model) {

        List<Role> excludedRoles = Arrays.asList(Role.ADMIN, Role.MANAGER, Role.PATIENT);
        List<Employee> allEmployees = employeeService.findAll();
        List<Employee> staffMembers = allEmployees.stream()
                .filter(emp -> !excludedRoles.contains(emp.getRole()))
                .collect(java.util.stream.Collectors.toList());
        

        for (Employee employee : staffMembers) {
            try {
                if (!userAccountService.hasUserAccount(employee)) {
                    userAccountService.createUserAccountForEmployee(employee);
                    System.out.println("Created user account for " + employee.getRole() + ": " + employee.getEmail());
                } else {

                    userAccountService.resetPasswordForEmployee(employee);
                    System.out.println("Reset password for " + employee.getRole() + ": " + employee.getEmail());
                }
            } catch (Exception e) {
                System.err.println("Failed to create/update user account for employee " + employee.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        

        List<Employee> doctors = staffMembers.stream()
                .filter(emp -> emp.getRole() == Role.DOCTOR)
                .collect(java.util.stream.Collectors.toList());
        List<Employee> receptionists = staffMembers.stream()
                .filter(emp -> emp.getRole() == Role.RECEPTIONIST)
                .collect(java.util.stream.Collectors.toList());
        List<Employee> financeStaff = staffMembers.stream()
                .filter(emp -> emp.getRole() == Role.FINANCE)
                .collect(java.util.stream.Collectors.toList());
        List<Employee> otherStaff = staffMembers.stream()
                .filter(emp -> emp.getRole() != Role.DOCTOR && 
                              emp.getRole() != Role.RECEPTIONIST && 
                              emp.getRole() != Role.FINANCE)
                .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("doctors", doctors);
        model.addAttribute("receptionists", receptionists);
        model.addAttribute("financeStaff", financeStaff);
        model.addAttribute("otherStaff", otherStaff);
        return "manager/staff_list";
    }

    @GetMapping("/manager/staff/edit/{id}")
    public String editStaff(@PathVariable Long id, Model model) {
        try {
            Employee employee = employeeService.findById(id);
            

            List<Role> excludedRoles = Arrays.asList(Role.ADMIN, Role.MANAGER, Role.PATIENT);
            if (excludedRoles.contains(employee.getRole())) {
                return "redirect:/manager/staff/list";
            }
        
        EmployeeForm form = new EmployeeForm();
        form.setId(employee.getId());
        form.setFirstName(employee.getFirstName());
        form.setFatherName(employee.getFatherName());
        form.setLastName(employee.getLastName());
        form.setPhoneNumber(employee.getPhoneNumber());
        form.setEmail(employee.getEmail());
        form.setAddress(employee.getAddress());
        form.setCity(employee.getCity());
        form.setState(employee.getState());
        form.setBirthDate(employee.getBirthDate());
        form.setGender(employee.getGender());
        form.setRole(employee.getRole());
        form.setSpecialization(employee.getSpecialization());
        form.setDepartmentId(employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null);
        form.setHireDate(employee.getHireDate());
        form.setSalary(employee.getSalary());
        form.setStatus(employee.getStatus() != null ? employee.getStatus().name() : "ACTIVE");
        
            model.addAttribute("employeeForm", form);
            model.addAttribute("departments", departmentService.findAllActive());
            

            List<Role> availableRoles = Arrays.asList(Role.values());
            List<Role> excludedRolesForEdit = Arrays.asList(Role.ADMIN, Role.MANAGER, Role.PATIENT);
            List<Role> allowedRoles = availableRoles.stream()
                    .filter(role -> !excludedRolesForEdit.contains(role))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("availableRoles", allowedRoles);
            
            return "manager/staff_edit";
        } catch (RuntimeException e) {
            return "redirect:/manager/staff/list";
        }
    }

    @PostMapping("/manager/staff/edit/{id}")
    public String updateStaff(@PathVariable Long id,
                             @ModelAttribute("employeeForm") @Valid EmployeeForm employeeForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        

        List<Role> excludedRoles = Arrays.asList(Role.ADMIN, Role.MANAGER, Role.PATIENT);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the validation errors in the form below.");
            model.addAttribute("departments", departmentService.findAllActive());
            

            List<Role> availableRoles = Arrays.asList(Role.values());
            List<Role> allowedRoles = availableRoles.stream()
                    .filter(role -> !excludedRoles.contains(role))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("availableRoles", allowedRoles);
            
            return "manager/staff_edit";
        }

        try {
            Employee existingEmployee = employeeService.findById(id);
            

            if (excludedRoles.contains(existingEmployee.getRole())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit " + existingEmployee.getRole() + " accounts through this interface.");
                return "redirect:/manager/staff/list";
            }
            

            if (employeeForm.getRole() == null) {
                employeeForm.setRole(existingEmployee.getRole());
            }
            
            if (employeeForm.getRole() == null || excludedRoles.contains(employeeForm.getRole())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid role. Cannot set role to " + (employeeForm.getRole() != null ? employeeForm.getRole() : "null") + ".");
                return "redirect:/manager/staff/edit/" + id;
            }


            existingEmployee.setFirstName(employeeForm.getFirstName());
            existingEmployee.setFatherName(employeeForm.getFatherName());
            existingEmployee.setLastName(employeeForm.getLastName());
            existingEmployee.setPhoneNumber(employeeForm.getPhoneNumber());
            existingEmployee.setEmail(employeeForm.getEmail());
            existingEmployee.setAddress(employeeForm.getAddress());
            existingEmployee.setCity(employeeForm.getCity());
            existingEmployee.setState(employeeForm.getState());
            existingEmployee.setBirthDate(employeeForm.getBirthDate());
            existingEmployee.setGender(employeeForm.getGender());
            existingEmployee.setRole(employeeForm.getRole());
            existingEmployee.setSpecialization(employeeForm.getSpecialization());
            existingEmployee.setHireDate(employeeForm.getHireDate());
            existingEmployee.setSalary(employeeForm.getSalary());
            existingEmployee.setStatus(Status.valueOf(employeeForm.getStatus().toUpperCase()));
            

            if (employeeForm.getDepartmentId() != null) {
                Department department = departmentService.findById(employeeForm.getDepartmentId())
                        .orElse(null);
                existingEmployee.setDepartment(department);
            }

            employeeService.save(existingEmployee);
            redirectAttributes.addFlashAttribute("successMessage", 
                    employeeForm.getRole().name() + " updated successfully!");
            return "redirect:/manager/staff/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update staff: " + e.getMessage());
            return "redirect:/manager/staff/edit/" + id;
        }
    }

    @GetMapping("/manager/staff/delete/{id}")
    public String deleteStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Employee employee = employeeService.findById(id);
            

            List<Role> excludedRoles = Arrays.asList(Role.ADMIN, Role.MANAGER, Role.PATIENT);
            if (excludedRoles.contains(employee.getRole())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete " + employee.getRole() + " accounts through this interface.");
                return "redirect:/manager/staff/list";
            }
            
            String employeeName = employee.getFirstName() + " " + employee.getLastName();
            String roleName = employee.getRole().name();
            

            try {
                userAccountService.deleteByEmployee(employee);
            } catch (Exception e) {

                System.out.println("UserAccount not found or already deleted for employee: " + id);
            }
            

            employeeService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    roleName + " '" + employeeName + "' deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to delete staff: " + e.getMessage());
        }
        return "redirect:/manager/staff/list";
    }

}

