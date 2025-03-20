package com.seabattle.Course_work.controllers;

import com.seabattle.Course_work.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Autowired
    private UserRepository userepository;

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("username", null);
        } else {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }

        model.addAttribute("title", "Главная страница");
        return "home";
    }

}