package com.Frontend.controller;

import com.Frontend.dto.LoginRequestDTO;
import com.Frontend.dto.MfaRequestDTO;
import com.Frontend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("login", new LoginRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("login") LoginRequestDTO dto, Model model) {
        String result = authService.login(dto);

        if ("MFA_REQUIRED".equals(result)) {
            model.addAttribute("mfa", new MfaRequestDTO());
            return "verify-mfa";
        } else if ("LOGIN_OK".equals(result)) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Login inválido");
            return "login";
        }
    }

    @PostMapping("/verify-mfa")
    public String verifyMfa(@ModelAttribute("mfa") MfaRequestDTO dto, Model model) {
        String result = authService.verifyMfa(dto);
        System.out.println("Resultado MFA: " + result);

        if ("ok".equals(result)) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Código MFA inválido");
            model.addAttribute("mfa", new MfaRequestDTO());
            return "verify-mfa";
        }
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("jwt", authService.getJwt());
        return "home";
    }

}
