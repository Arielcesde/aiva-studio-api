package com.aivastudio.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aivastudio.model.dto.CreditResponse;
import com.aivastudio.model.dto.RechargeRequest;
import com.aivastudio.model.dto.RechargeResponse;
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

    @GetMapping("/{userId}/movimientos")
    public List<CreditResponse> listMovements(@PathVariable long userId) {
        return creditService.listMovements(userId);
    }

    @PostMapping("/recargar")
    public ResponseEntity<RechargeResponse> recharge(@Valid @RequestBody RechargeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditService.recharge(request));
    }
}