package com.seabattle.Course_work.controllers;

import com.seabattle.Course_work.models.User;
import com.seabattle.Course_work.repositories.UserRepository;
import com.seabattle.Course_work.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final UserRepository userRepository;

    public RegistrationController(UserService userService,UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping
    public String registerUser(@RequestParam String login,
                               @RequestParam String password,
                               @RequestParam String repeat_password,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (userRepository.findByLogin(login).isPresent()) {
            model.addAttribute("error", "Такой пользователь уже существует");
            return "register";
        }
        if (!password.equals(repeat_password)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "register";
        }

        try {
            userService.saveUser(login, password);
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            return "register";
        }
        redirectAttributes.addFlashAttribute("sucsess","успех");
        return "redirect:/login";
    }
}