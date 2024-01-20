package net.springboot.controller;

import net.springboot.model.User;
import net.springboot.repository.UserRepository;
import net.springboot.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);


    public AppController(UserRepository userRepo, UserRoleService userRoleService) {
        this.userRepo = userRepo;
        this.userRoleService = userRoleService;
    }


    @GetMapping("")
    public String viewHomePage(Model model) {
        try {
            List<User> listUsers = userRepo.findAll();
            model.addAttribute("listUsers", listUsers);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAuthenticated = !auth.getName().equals("anonymousUser");

            String email = auth.getName();
            String userRole = userRoleService.getUserRole(email);
            model.addAttribute("userRole", userRole);
            model.addAttribute("isAuthenticated", isAuthenticated);

            logger.info("Home page accessed by user with role: {}", userRole);

            return "index";
        } catch (Exception e) {
            logger.error("An error occurred while processing home page request", e);
            return "error"; // Предположим, у вас есть страница ошибки с именем "error"
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        logger.info("Profile page accessed for user: {}", email);

        User user = userRepo.findByEmail(email);

        model.addAttribute("user", user);

        return "profile";
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Registration form accessed");
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(@ModelAttribute("user") User user) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            user.setRegistrationDate(new Date());
            user.setRole("USER");
            userRepo.save(user);

            logger.info("User registered successfully: {}", user.getEmail());

            return "register_success";
        } catch (Exception e) {
            logger.error("An error occurred while processing user registration", e);
            return "error";
        }
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        try {
            List<User> listUsers = userRepo.findAll();
            model.addAttribute("listUsers", listUsers);
            logger.info("User list page accessed");
            return "users";
        } catch (Exception e) {
            logger.error("An error occurred while processing user list page request", e);
            return "error";
        }
    }

    @GetMapping("/login")
    public String login() {
        logger.info("Login page accessed");
        return "login";
    }
}

