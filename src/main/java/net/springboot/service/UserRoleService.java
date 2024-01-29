package net.springboot.service;

import net.springboot.model.User;
import net.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    private final UserRepository userRepository;

    @Autowired
    public UserRoleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void blockUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setBanned(true);
            userRepository.save(user);
            System.out.println("User blocked: " + email);
        }
    }

    public String getUserRole(String email) {
        User user = userRepository.findByEmail(email);
        return (user != null) ? user.getRole() : null;
    }

    public void banUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setBanned(true);
            userRepository.save(user);
        }
    }

    public void unbanUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setBanned(false);
            userRepository.save(user);
        }
    }

    public boolean isUserBanned(String email) {
        User user = userRepository.findByEmail(email);
        return user != null && user.isBanned();
    }
}