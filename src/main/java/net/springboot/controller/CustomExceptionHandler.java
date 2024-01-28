package net.springboot.controller;

import net.springboot.service.BannedUserException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BannedUserException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleBannedUserException(BannedUserException ex, Model model) {
        model.addAttribute("error", "User is banned");
        return "error";
    }

}
