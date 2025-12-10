package com.bootispringu.dentalsystemmenagment.Controller;

import com.bootispringu.dentalsystemmenagment.Entity.Department;
import com.bootispringu.dentalsystemmenagment.Entity.DepartmentType;
import com.bootispringu.dentalsystemmenagment.Entity.Status;
import com.bootispringu.dentalsystemmenagment.Service.DepartmentService;
import com.bootispringu.dentalsystemmenagment.Service.DepartmentTypeService;
import com.bootispringu.dentalsystemmenagment.dto.DepartmentForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final DepartmentService departmentService;
    private final DepartmentTypeService departmentTypeService;

    public AdminController(DepartmentService departmentService, DepartmentTypeService departmentTypeService) {
        this.departmentService = departmentService;
        this.departmentTypeService = departmentTypeService;
    }


    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        return "admin/home";
    }


    
    @GetMapping("/admin/department/add")
    public String addDepartment(Model model) {
        model.addAttribute("departmentForm", new DepartmentForm());
        return "admin/department_add";
    }

    @PostMapping("/admin/department/add")
    public String saveDepartment(@ModelAttribute("departmentForm") @Valid DepartmentForm departmentForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the validation errors in the form below.");
            return "admin/department_add";
        }

        try {

            DepartmentType departmentType = departmentTypeService.findByName(departmentForm.getDepartmentTypeName().trim())
                    .orElse(null);
            

            if (departmentType == null) {
                departmentType = new DepartmentType();
                departmentType.setName(departmentForm.getDepartmentTypeName().trim());
                departmentType.setStatus(Status.ACTIVE);
                departmentType = departmentTypeService.save(departmentType);
            }
            
            Department department = new Department();
            department.setName(departmentForm.getName());
            department.setDescription(departmentForm.getDescription());
            department.setLocation(departmentForm.getLocation());
            department.setDepartmentType(departmentType);
            department.setCategory(departmentForm.getCategory());
            department.setStatus(Status.valueOf(departmentForm.getStatus().toUpperCase()));

            departmentService.save(department);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Department '" + department.getName() + "' created successfully!");
            return "redirect:/admin/department/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to create department: " + e.getMessage());
            return "redirect:/admin/department/add";
        }
    }

    @GetMapping("/admin/department/list")
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        return "admin/department_list";
    }

    @GetMapping("/admin/department/edit/{id}")
    public String editDepartment(@PathVariable Long id, Model model) {
        Department department = departmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        DepartmentForm form = new DepartmentForm();
        form.setDepartmentId(department.getDepartmentId());
        form.setName(department.getName());
        form.setDescription(department.getDescription());
        form.setLocation(department.getLocation());
        form.setDepartmentTypeName(department.getDepartmentType() != null ? department.getDepartmentType().getName() : "");
        form.setCategory(department.getCategory());
        form.setStatus(department.getStatus().name());
        
        model.addAttribute("departmentForm", form);
        return "admin/department_edit";
    }

    @PostMapping("/admin/department/edit/{id}")
    public String updateDepartment(@PathVariable Long id,
                                   @ModelAttribute("departmentForm") @Valid DepartmentForm departmentForm,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors.");
            return "redirect:/admin/department/edit/" + id;
        }

        try {

            DepartmentType departmentType = departmentTypeService.findByName(departmentForm.getDepartmentTypeName().trim())
                    .orElse(null);
            

            if (departmentType == null) {
                departmentType = new DepartmentType();
                departmentType.setName(departmentForm.getDepartmentTypeName().trim());
                departmentType.setStatus(Status.ACTIVE);
                departmentType = departmentTypeService.save(departmentType);
            }
            
            Department department = new Department();
            department.setName(departmentForm.getName());
            department.setDescription(departmentForm.getDescription());
            department.setLocation(departmentForm.getLocation());
            department.setDepartmentType(departmentType);
            department.setCategory(departmentForm.getCategory());
            department.setStatus(Status.valueOf(departmentForm.getStatus().toUpperCase()));

            departmentService.update(id, department);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Department updated successfully!");
            return "redirect:/admin/department/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update department: " + e.getMessage());
            return "redirect:/admin/department/edit/" + id;
        }
    }

    @GetMapping("/admin/department/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to delete department: " + e.getMessage());
        }
        return "redirect:/admin/department/list";
    }



    
    @GetMapping("/admin/supplies")
    public String manageSupplies(Model model) {
        model.addAttribute("featureName", "Supplies Management");
        model.addAttribute("backUrl", "/admin/home");
        return "coming_soon";
    }
}

