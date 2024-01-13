package com.example.springboot;

import com.example.springboot.entity.UserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserLogController {

    private final UserLogRepository userLogRepository;

    @Autowired
    public UserLogController(UserLogRepository userLogRepository) {
        this.userLogRepository = userLogRepository;
    }

    @GetMapping("/")
    public String getUserLog(Model model) {
        List<UserLog> userLogs = userLogRepository.findAll();
        model.addAttribute("data", userLogs);
        return "home"; //
    }
}
