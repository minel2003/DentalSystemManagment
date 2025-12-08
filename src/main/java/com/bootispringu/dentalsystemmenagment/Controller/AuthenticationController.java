package com.bootispringu.dentalsystemmenagment.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/acces_denied")
    public String accessDenied() {
        return "acces_denied";
    }
}
