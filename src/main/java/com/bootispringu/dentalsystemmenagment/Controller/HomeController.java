package com.bootispringu.dentalsystemmenagment.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Public homepage
    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }
}