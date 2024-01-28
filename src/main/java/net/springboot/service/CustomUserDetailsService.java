package net.springboot.service;

import net.springboot.controller.AppController;
import net.springboot.model.User;
import net.springboot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        if (user != null) {
            user.setRegistrationDate(new Date());
            userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(email);

            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }

            if (user.isBanned()) {
                logger.info("User {} is banned", email);
                throw new BannedUserException("User is banned");
            }

            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole()));

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
        } catch (BannedUserException e) {
            throw new UsernameNotFoundException("User is banned", e);
        } catch (Exception e) {
            logger.error("Exception in loadUserByUsername", e);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}

