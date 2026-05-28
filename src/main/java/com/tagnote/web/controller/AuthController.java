package com.tagnote.web.controller;

import com.tagnote.web.entities.User;
import com.tagnote.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register-page")
    public String registerPage(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.register(username, email, password);
            redirectAttributes.addAttribute("success", "Регистрация успешна! Теперь войдите.");
            return "redirect:/index.html";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/index.html";
        }
    }
}
