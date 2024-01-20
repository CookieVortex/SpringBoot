package net.springboot.service;

import net.springboot.model.User;
import net.springboot.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private UserRepository userRepository;

    public void registerUser(User user) {
        if (user != null) {
            user.setRegistrationDate(new Date());
            userRepository.save(user);
        }
    }

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        String role = user.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority(role);

        return new CustomUserDetails(user, Collections.singletonList(authority));
    }
}

