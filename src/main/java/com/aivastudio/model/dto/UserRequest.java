package com.aivastudio.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String nombre,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico no es válido")
        @Size(max = 254, message = "El correo electrónico no puede superar 254 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
        String password) {
}
