package com.aivastudio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aivastudio.model.dto.UserResponse;
import com.aivastudio.service.CreditService;

@RestController
@RequestMapping("/api/aiva/usuarios")
public class UserController {

    private final CreditService creditService;

    public UserController(CreditService creditService) {
        this.creditService = creditService;
    }

    @GetMapping("/{userId}")
    public UserResponse getProfile(@PathVariable long userId) {
        return creditService.getProfile(userId);
    }
}