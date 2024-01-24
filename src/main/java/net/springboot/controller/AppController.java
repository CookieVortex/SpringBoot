package net.springboot.controller;

import net.springboot.model.User;
import net.springboot.repository.UserRepository;
import net.springboot.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userRepo.deleteById(id);
            return "redirect:/users";
        } catch (Exception e) {
            return "error";
        }
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
            return "error";
        }
    }


    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        logger.info("Profile page accessed for user: {}", email);

        User user = userRepo.findByEmail(email);

        model.addAttribute("user", user);

        boolean isAuthenticated = !auth.getName().equals("anonymousUser");


        String userRole = userRoleService.getUserRole(email);
        model.addAttribute("userRole", userRole);
        model.addAttribute("isAuthenticated", isAuthenticated);

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
    public String listUsers(Model model,
                            @RequestParam(name = "search", required = false) String search,
                            @RequestParam(name = "sort", required = false, defaultValue = "lastName") String sort,
                            @RequestParam(name = "page", defaultValue = "0") int currentPage) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userRole = userRoleService.getUserRole(auth.getName());

            logger.info("User role: {}", userRole);

            if ("ADMIN".equals(userRole)) {
                boolean isAuthenticated = !auth.getName().equals("anonymousUser");

                Page<User> userPage;
                Pageable pageable = PageRequest.of(currentPage, 10);

                if (search != null && !search.isEmpty()) {
                    userPage = userRepo.findByFirstNameContainingOrLastNameContainingOrEmailContaining(search, search, search, pageable);
                } else {
                    switch (sort) {
                        case "firstName":
                            userPage = userRepo.findAllByOrderByFirstNameAsc(pageable);
                            break;
                        case "lastName":
                            userPage = userRepo.findAllByOrderByLastNameAsc(pageable);
                            break;
                        case "email":
                            userPage = userRepo.findAllByOrderByEmailAsc(pageable);
                            break;
                        case "role":
                            userPage = userRepo.findAllByOrderByRoleAsc(pageable);
                            break;
                        default:
                            userPage = userRepo.findAllByOrderByLastNameAscFirstNameAsc(pageable);
                            break;
                    }
                }

                model.addAttribute("userRole", userRole);
                model.addAttribute("isAuthenticated", isAuthenticated);
                model.addAttribute("listUsers", userPage.getContent());

                model.addAttribute("currentPage", userPage.getNumber());
                model.addAttribute("totalPages", userPage.getTotalPages());

                logger.info("User list page accessed");
                return "users";
            } else {
                logger.warn("Access denied: User does not have the required role");
                return "access-denied";
            }
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

