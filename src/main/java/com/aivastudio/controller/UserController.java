package com.aivastudio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aivastudio.model.dto.AuthResponse;
import com.aivastudio.model.dto.UserRequest;
import com.aivastudio.model.dto.UserResponse;
import com.aivastudio.service.AuthService;
import com.aivastudio.service.CreditService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Registro y consulta de usuarios")
public class UserController {

    private final AuthService authService;
    private final CreditService creditService;

    public UserController(AuthService authService, CreditService creditService) {
        this.authService = authService;
        this.creditService = creditService;
    }

    @PostMapping
    @Operation(summary = "Registrar un usuario",
            description = "Crea la cuenta con 100 créditos iniciales y devuelve un token Bearer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya registrado")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Consultar el perfil del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public UserResponse getProfile(@PathVariable long userId) {
        return creditService.getProfile(userId);
    }
}