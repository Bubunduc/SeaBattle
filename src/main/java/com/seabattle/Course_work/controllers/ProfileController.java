package com.seabattle.Course_work.controllers;

import com.seabattle.Course_work.models.Result;
import com.seabattle.Course_work.models.User;
import com.seabattle.Course_work.repositories.UserRepository;
import com.seabattle.Course_work.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.seabattle.Course_work.repositories.ResultRepository;
import java.security.Principal;
import java.util.List;

@Controller
public class ProfileController {
    @Autowired
    ResultRepository resultRepository;

    @Autowired
    UserRepository userRepository;
    @GetMapping("/profile")
    public String profile(Model model, Principal principal)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("username", null);
        } else {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("username", principal.getName());
        System.out.println(principal.getName());
        User user = userRepository.findByLogin(principal.getName()).get();
        if ((Integer)user.getDefeats() ==null){
        model.addAttribute("defeats", 0);

        }
        else
        {
            model.addAttribute("defeats", user.getDefeats());
        }
        if ((Integer)user.getWins() == null)
        {
            model.addAttribute("wins", 0);
        }
        else
        {
            model.addAttribute("wins", user.getWins());
        }
        List<Result> results = resultRepository.findByWinnerOrLoser( principal.getName(),  principal.getName());
        model.addAttribute("results", results);
        return "profile";
    }

}
