package net.springboot.service;

import org.springframework.security.authentication.DisabledException;

public class BannedUserException extends DisabledException {

    public BannedUserException(String msg) {
        super(msg);
    }
}

