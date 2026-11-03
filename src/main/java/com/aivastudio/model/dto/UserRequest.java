package com.aivastudio.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank(message = "El nombre es obligatorio y no puede superar 80 caracteres")
        String nombre,
        @NotBlank(message = "El correo electrónico no es válido")
        String email,
        @NotNull(message = "La contraseña debe tener entre 6 y 72 caracteres")
        String password) {
}