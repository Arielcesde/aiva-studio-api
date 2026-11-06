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

import com.aivastudio.model.dto.CreditMovementResponse;
import com.aivastudio.model.dto.CreditRechargeRequest;
import com.aivastudio.model.dto.CreditRechargeResponse;
import com.aivastudio.service.CreditService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/{userId}/credits")
@Tag(name = "Credits", description = "Movimientos y recarga de créditos")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @GetMapping
    @Operation(summary = "Listar los movimientos de créditos del usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimientos del más reciente al más antiguo"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public List<CreditMovementResponse> listMovements(@PathVariable long userId) {
        return creditService.listMovements(userId);
    }

    @GetMapping("/total")
    @Operation(summary = "Consultar el total de créditos acreditados al usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total acumulado de recargas"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public long totalCredited(@PathVariable long userId) {
        return creditService.totalCredited(userId);
    }

    @PostMapping
    @Operation(summary = "Recargar créditos",
            description = "Mínimo 50 créditos. Desde 500 créditos se aplica un bono del 20%.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recarga registrada"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida o saldo máximo superado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<CreditRechargeResponse> recharge(
            @PathVariable long userId,
            @Valid @RequestBody CreditRechargeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditService.recharge(userId, request));
    }
}