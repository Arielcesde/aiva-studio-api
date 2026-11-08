package com.aivastudio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aivastudio.model.dto.PasswordResetConfirmRequest;
import com.aivastudio.model.dto.PasswordResetRequest;
import com.aivastudio.service.PasswordResetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/password-resets")
@Tag(name = "Password resets", description = "Recuperación de contraseña mediante enlace enviado por correo")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping
    @Operation(summary = "Solicitar recuperación de contraseña",
            description = "Envía un enlace al correo si existe una cuenta. Responde igual exista o no, para no revelar qué correos están registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Solicitud recibida"),
            @ApiResponse(responseCode = "400", description = "Correo con formato inválido")
    })
    public ResponseEntity<Void> request(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.requestReset(request.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/confirm")
    @Operation(summary = "Restablecer la contraseña con el token recibido por correo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contraseña actualizada"),
            @ApiResponse(responseCode = "400", description = "Token inválido, vencido o contraseña no válida")
    })
    public ResponseEntity<Void> confirm(@Valid @RequestBody PasswordResetConfirmRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}