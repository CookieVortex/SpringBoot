package net.springboot.controller;

import net.springboot.model.User;
import net.springboot.repository.UserRepository;
import net.springboot.service.UserRoleService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;

@Controller
public class AppController {

    private final UserRepository userRepo;
    private final UserRoleService userRoleService;

    public AppController(UserRepository userRepo, UserRoleService userRoleService) {
        this.userRepo = userRepo;
        this.userRoleService = userRoleService;
    }


    @GetMapping("")
    public String viewHomePage(Model model) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = !auth.getName().equals("anonymousUser");

        String email = auth.getName();
        String userRole = userRoleService.getUserRole(email);
        model.addAttribute("userRole", userRole);
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "index";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepo.findByEmail(email);

        model.addAttribute("user", user);

        return "profile";
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(@ModelAttribute("user") User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setRegistrationDate(new Date());
        user.setRole("USER");
        userRepo.save(user);

        return "register_success";
    }


    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

