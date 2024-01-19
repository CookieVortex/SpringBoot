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

    public String getUserRole(String email) {
        User user = userRepository.findByEmail(email);
        return (user != null) ? user.getRole() : null;
    }
}
