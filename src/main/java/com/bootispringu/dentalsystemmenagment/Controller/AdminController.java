package com.bootispringu.dentalsystemmenagment.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // ================== HOME ==================
    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin/home";
    }
}

