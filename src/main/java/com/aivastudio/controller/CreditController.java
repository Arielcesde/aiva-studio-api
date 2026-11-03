package com.aivastudio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aivastudio.model.dto.CreditRechargeRequest;
import com.aivastudio.model.dto.CreditResponse;
import com.aivastudio.service.CreditService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/aiva/usuarios")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @GetMapping("/{userId}/movimientos")
    public List<CreditResponse> listMovements(@PathVariable long userId) {
        return creditService.listMovements(userId);
    }

    @PostMapping("/recargar")
    public ResponseEntity<CreditResponse> recharge(@Valid @RequestBody CreditRechargeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditService.recharge(request));
    }
}
