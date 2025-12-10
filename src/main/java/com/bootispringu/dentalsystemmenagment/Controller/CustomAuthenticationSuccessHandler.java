package com.bootispringu.dentalsystemmenagment.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/";

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }
            if (role.equals("RECEPTIONIST")) {
                redirectUrl = "/receptionist/home";
                break;
            } else if (role.equals("PATIENT")) {
                redirectUrl = "/patient/home";
                break;
            } else if (role.equals("DOCTOR")) {
                redirectUrl = "/doctor/home";
                break;
            } else if (role.equals("ADMIN")) {
                redirectUrl = "/admin/home";
                break;
            } else if (role.equals("MANAGER")) {
                redirectUrl = "/manager/home";
                break;
            } else if (role.equals("FINANCE")) {
                redirectUrl = "/finance/home";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
