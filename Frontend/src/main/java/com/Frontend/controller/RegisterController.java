package com.Frontend.controller;

import org.springframework.ui.Model;
import com.Frontend.dto.RegisterDTO;
import com.Frontend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final AuthService authService;

    @GetMapping("/register")
    public String form(Model model) {
        model.addAttribute("registro", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String submit(@ModelAttribute("registro") RegisterDTO dto, Model model) {
        Map<String, String> result = authService.register(dto);
        if (result.containsKey("error")) {
            model.addAttribute("error", result.get("error"));
            return "register";
        }
        model.addAttribute("secret", result.get("secret"));
        model.addAttribute("qr", result.get("qr"));
        return "mfa-qr";
    }

}
